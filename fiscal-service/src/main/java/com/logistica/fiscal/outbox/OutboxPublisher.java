package com.logistica.fiscal.outbox;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

/**
 * Lê periodicamente os eventos pendentes da tabela outbox_event usando
 * "FOR UPDATE SKIP LOCKED", o que permite ter VÁRIAS instâncias deste serviço
 * rodando em paralelo sem que uma trave a outra: cada instância pega um lote
 * diferente de linhas e simplesmente pula as que já estão sendo processadas.
 *
 * O processamento do lote roda sobre um Scheduler apoiado em Virtual Threads
 * (Java 21 / Project Loom), ideal aqui porque a publicação no Kafka e a
 * atualização de status no banco intercalam I/O sem bloquear threads de
 * plataforma.
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private static  final int TAMANHO_LOTE = 20;

    private final DatabaseClient databaseClient;
    private final KafkaTemplate<String , String> kafkaTemplate;
    private final Scheduler virtualThreadScheduler =
            Schedulers.fromExecutorService(Executors.newVirtualThreadPerTaskExecutor(), "outbox-publisher");

    public OutboxPublisher(DatabaseClient databaseClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.databaseClient = databaseClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    public void publicarPendentes() {
        buscarLotePendente()
                .publishOn(virtualThreadScheduler)
                .flatMap(this::publicarEMarcar, 8)
                .doOnError(erro -> log.error("Falha ao processar lote do outbox", erro))
                .subscribe();
    }


    private Flux<OutboxEvent> buscarLotePendente() {
        // FOR UPDATE SKIP LOCKED: cada instância pega linhas livres, sem esperar
        // outra instância liberar o lock (essencial ao escalar horizontalmente).
        return databaseClient.sql("""
                        SELECT id, agregado_id, tipo_evento, payload, status, criado_em
                        FROM outbox_event
                        WHERE status = 'PENDENTE'
                        ORDER BY criado_em
                        LIMIT :tamanhoLote
                        FOR UPDATE SKIP LOCKED
                        """)
                .bind("tamanhoLote", TAMANHO_LOTE)
                .map((row, meta) -> OutboxEvent.de(
                        row.get("agregado_id", java.util.UUID.class),
                        row.get("tipo_evento", String.class),
                        row.get("payload", String.class)))
                .all();
    }

    private reactor.core.publisher.Mono<Void> publicarEMarcar(OutboxEvent evento) {
        return reactor.core.publisher.Mono.fromRunnable(() ->
                        kafkaTemplate.send("fiscal.nota-fiscal.eventos", evento.getAgregadoId().toString(), evento.getPayload()))
                .then(databaseClient.sql("UPDATE outbox_event SET status = 'PUBLICADO' WHERE id = :id")
                        .bind("id", evento.getId())
                        .fetch()
                        .rowsUpdated())
                .doOnSuccess(v -> log.debug("Evento {} publicado para agregado {}", evento.getTipoEvento(), evento.getAgregadoId()))
                .then();
    }
}

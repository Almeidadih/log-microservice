package com.logistica.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Mesmo mecanismo do OutboxPublisher do fiscal-service: lê os eventos
 * PENDENTE com "FOR UPDATE SKIP LOCKED" (permite múltiplas instâncias sem
 * conflito) e publica no Kafka — aqui usando KafkaSender (reactor-kafka) em
 * vez de KafkaTemplate, já que este serviço adotou a API 100% reativa do
 * Kafka desde o consumidor.
 *
 * Este publisher agora lida com DOIS tipos de evento (COLETA_REGISTRADA e
 * ENTREGA_CONCLUIDA), cada um indo para um tópico diferente — o roteamento
 * é feito pelo tipo_evento gravado na própria linha do outbox
 * (ver TOPICO_POR_TIPO_EVENTO). O mecanismo de garantia (outbox + SKIP
 * LOCKED) continua o mesmo para os dois; só muda o destino.
 */
@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private static final int TAMANHO_LOTE = 20;

    private static final Map<String, String> TOPICO_POR_TIPO_EVENTO = Map.of(
            "COLETA_REGISTRADA", "logistica.coleta.eventos",
            "ENTREGA_CONCLUIDA", "logistica.entrega.eventos",
            "COLETA_CANCELADA", "logistica.cancelamento.eventos"
    );

    private final DatabaseClient databaseClient;
    private final KafkaSender<String, String> kafkaSender;
    private final Scheduler virtualThreadScheduler =
            Schedulers.fromExecutorService(Executors.newVirtualThreadPerTaskExecutor(), "outbox-publisher-frota");

    public OutboxPublisher(DatabaseClient databaseClient, KafkaSender<String, String> kafkaSender) {
        this.databaseClient = databaseClient;
        this.kafkaSender = kafkaSender;
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
                        row.get("agregado_id", UUID.class),
                        row.get("tipo_evento", String.class),
                        row.get("payload", String.class)))
                .all();
    }

    private Mono<Void> publicarEMarcar(OutboxEvent evento) {
        String topico = TOPICO_POR_TIPO_EVENTO.get(evento.getTipoEvento());
        if (topico == null) {
            log.error("Tipo de evento sem tópico configurado: {} (evento {} não será publicado)",
                    evento.getTipoEvento(), evento.getId());
            return Mono.empty();
        }

        SenderRecord<String, String, UUID> record = SenderRecord.create(
                new org.apache.kafka.clients.producer.ProducerRecord<>(
                        topico, evento.getAgregadoId().toString(), evento.getPayload()),
                evento.getId());

        return kafkaSender.send(Mono.just(record))
                .next()
                .then(databaseClient.sql("UPDATE outbox_event SET status = 'PUBLICADO' WHERE id = :id")
                        .bind("id", evento.getId())
                        .fetch()
                        .rowsUpdated())
                .doOnSuccess(v -> log.debug("Evento {} publicado em {} para agregado {}",
                        evento.getTipoEvento(), topico, evento.getAgregadoId()))
                .then();
    }
}

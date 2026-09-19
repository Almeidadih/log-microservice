package com.logistica.service;

import com.logistica.domain.NotaFiscalId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class NotaFiscalPorjecaoService {

    private static final Logger log = LoggerFactory.getLogger(NotaFiscalPorjecaoService.class);

    private final DatabaseClient databaseClient;

    public NotaFiscalPorjecaoService(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }


    /**
     * Upsert (insere ou atualiza) a projeção local da nota fiscal.
     *
     * Usamos "INSERT ... ON CONFLICT (id) DO UPDATE" em vez de repository.save()
     * porque o id da NotaFiscalRegistrada é ATRIBUÍDO (vem do evento), não
     * GERADO pelo banco — nesse caso o Spring Data R2DBC não tem como saber
     * sozinho se deve fazer INSERT ou UPDATE (isso é resolvido implementando a
     * interface Persistable, ou, como fizemos aqui, escrevendo o upsert na mão
     * com SQL nativo do Postgres, que é direto e explícito).
     */
    public Mono<Void> upsert(NotaFiscalId id , String status) {
        return databaseClient.sql("""
                INSERT INTO nota_fiscal_registrada (id, status)
                VALUES (:id, :status)
                ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.starus
                """)
                .bind("id" , id.value())
                .bind("status", status)
                .fetch()
                .rowsUpdated()
                .doOnSuccess(linhas -> log.info("Projeção da nota fiscal {} atualizada para status {} ", id, status))
                .then();
    }
}

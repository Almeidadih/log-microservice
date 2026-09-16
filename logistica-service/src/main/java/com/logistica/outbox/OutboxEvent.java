package com.logistica.outbox;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * Mesmo padrão de transactional outbox do fiscal-service: a Coleta e o
 * evento correspondente são gravados na mesma transação; um publisher
 * separado (OutboxPublisher) lê os pendentes com SKIP LOCKED e publica no
 * Kafka de forma assíncrona e resiliente.
 */
@Table("outbok_event")
public class OutboxEvent {
    @Id
    @Column("id")
    private UUID id;

    @Column("agregado_id")
    private UUID agregadoId;

    @Column("tipo_evento")
    private  String tipoEvento;

    @Column("payload")
    private String payload;

    @Column("status")
    private String status;

    @Column("criado_em")
    private OffsetDateTime criadoEm;

    protected OutboxEvent() {

    }

    private OutboxEvent(UUID agregadoId, String tipoEvento, String payload) {
        this.id = UUID.randomUUID();
        this.agregadoId = agregadoId;
        this.tipoEvento = tipoEvento;
        this.payload = payload;
        this.status = "PENDENTE";
        this.criadoEm = OffsetDateTime.now();
    }
    public static OutboxEvent de(UUID agregadoId, String tipoEvento, String payload) {
        return new OutboxEvent(agregadoId, tipoEvento, payload);
    }

    public UUID getId() {
        return id;
    }

    public UUID getAgregadoId() {
        return agregadoId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}

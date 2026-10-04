package com.logistica.fiscal.outbox;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Registro do padrão transactional outbox: toda mudança relevante na nota fiscal
 * é gravada aqui na MESMA transação do banco. Um processo separado (OutboxPublisher)
 * lê essa tabela com "FOR UPDATE SKIP LOCKED" e publica no Kafka, garantindo que
 * nunca perdemos um evento mesmo se o serviço cair entre salvar e publicar.
 */
@Table("outbox_event")
public class OutboxEvent {

    @Id
    @Column("id")
    private UUID id;

    @Column("agregado_id")
    private UUID agregadoId;

    @Column("tipo_evento")
    private String tipoEvento;

    @Column("payload")
    private String payload;

    @Column("status")
    private String status;

    @Column("criado_em")
    private OffsetDateTime criadoEm;

    public OutboxEvent() {
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

    public void marcarPublicado() {
        this.status = "PUBLICADO";
    }

}

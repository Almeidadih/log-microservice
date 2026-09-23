package com.logistica.fiscal.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Table("nota_fiscal")
public class NotaFiscal {

    @Id
    @Column("id")
    private NotaFiscalId id;

    @Column("customer_id")
    private CustomerId customerId;

    @Column("transportadora_id")
    private TransportadoraId transportadoraId;

    @Column("numero")
    private String numero;

    @Column("valor")
    private BigDecimal valor;

    @Column("status")
    private StatusNotaFiscal status;

    @Column("criada_em")
    private OffsetDateTime criadaEm;

    @Column("atualizada_em")
    private OffsetDateTime atualizadaEm;

    @Version
    @Column("versao")
    private Long versao;

    protected NotaFiscal() {
        // exigido pelo Spring Data
    }

    private NotaFiscal(NotaFiscalId id, CustomerId customerId, String numero, BigDecimal valor) {
        this.id = id;
        this.customerId = customerId;
        this.numero = numero;
        this.valor = valor;
        this.status = StatusNotaFiscal.PENDENTE;
        this.criadaEm = OffsetDateTime.now();
        this.atualizadaEm = OffsetDateTime.now();
    }

    public static NotaFiscal registrar(CustomerId customerId, String numero, BigDecimal valor) {
        return new NotaFiscal(NotaFiscalId.novo(), customerId, numero, valor);
    }

    /**
     * Aplica uma transição de status validando o ciclo de vida da nota fiscal.
     * Lança StatusInvalidoException se a transição não for permitida.
     */
    public void alterarStatus(StatusNotaFiscal novoStatus) {
        if (!novoStatus.podeTransicionarDe(this.status)) {
            throw new com.logistica.fiscal.exception.StatusInvalidoException(this.id, this.status, novoStatus);
        }
        this.status = novoStatus;
        this.atualizadaEm = OffsetDateTime.now();
    }

    public void atribuirTransportadora(TransportadoraId transportadoraId) {
        this.transportadoraId = transportadoraId;
        this.atualizadaEm = OffsetDateTime.now();
    }

    public NotaFiscalId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public TransportadoraId getTransportadoraId() {
        return transportadoraId;
    }

    public String getNumero() {
        return numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public StatusNotaFiscal getStatus() {
        return status;
    }

    public OffsetDateTime getCriadaEm() {
        return criadaEm;
    }

    public OffsetDateTime getAtualizadaEm() {
        return atualizadaEm;
    }
}

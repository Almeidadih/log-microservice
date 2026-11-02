package com.logistica.avaliacao.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

/**
 * Cópia local (read model) do mínimo que este serviço precisa saber sobre
 * uma coleta: quem transportou, em qual região, e quando foi coletada.
 * Alimentada pelo ColetaEventConsumer a partir do tópico
 * logistica.coleta.eventos. O coletadaEm existe aqui especificamente para
 * o cálculo automático da nota de agilidade quando a entrega é concluída
 * (ver EntregaProcessamentoListener): sem ele, não teríamos como saber
 * quanto tempo a transportadora levou.
 */
@Table("coleta_projetada")
public class ColetaProjetada {

    @Id
    @Column("id")
    private ColetaId id;

    @Column("transportadora_id")
    private TransportadoraId transportadoraId;

    @Column("cidade")
    private String cidade;

    @Column("uf")
    private String uf;

    @Column("coletada_em")
    private OffsetDateTime coletadaEm;

    protected ColetaProjetada() {
    }

    public ColetaProjetada(ColetaId id, TransportadoraId transportadoraId, String cidade, String uf,
                           OffsetDateTime coletadaEm) {
        this.id = id;
        this.transportadoraId = transportadoraId;
        this.cidade = cidade;
        this.uf = uf;
        this.coletadaEm = coletadaEm;
    }

    public ColetaId getId() { return id; }
    public TransportadoraId getTransportadoraId() { return transportadoraId; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public OffsetDateTime getColetadaEm() { return coletadaEm; }
}

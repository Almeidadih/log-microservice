package com.logistica.avaliacao.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("avaliacao")
public class Avaliacao {


    @Id
    @Column("id")
    private AvaliacaoId id;

    @Column("coleta_id")
    private ColetaId coletaId;

    @Column("transportadora_id")
    private TransportadoraId transportadoraId;

    @Column("cidade")
    private String cidade;

    @Column("uf")
    private String uf;

    @Column("nota_qualidade")
    private int notaQualidade;

    @Column("nota_agilidade")
    private int notaAgilidade;

    @Column("origem")
    private OrigemAvaliacao origem;

    @Column("criada_em")
    private OffsetDateTime criadaEm;

    @Column("cancelada_em")
    private OffsetDateTime canceladaEm;

    protected Avaliacao() {
    }

    private Avaliacao(AvaliacaoId id, ColetaId coletaId, TransportadoraId transportadoraId,
                      String cidade, String uf, int notaQualidade, int notaAgilidade, OrigemAvaliacao origem) {
        this.id = id;
        this.coletaId = coletaId;
        this.transportadoraId = transportadoraId;
        this.cidade = cidade;
        this.uf = uf;
        this.notaQualidade = notaQualidade;
        this.notaAgilidade = notaAgilidade;
        this.origem = origem;
        this.criadaEm = OffsetDateTime.now();
    }

    /** Avaliação lançada manualmente via API (ex: pesquisa de satisfação com o cliente). */
    public static Avaliacao registrarManual(ColetaProjetada coleta, int notaQualidade, int notaAgilidade) {
        return new Avaliacao(AvaliacaoId.novo(), coleta.getId(), coleta.getTransportadoraId(),
                coleta.getCidade(), coleta.getUf(), notaQualidade, notaAgilidade, OrigemAvaliacao.MANUAL);
    }

    /**
     * Avaliação gerada automaticamente a partir do evento ENTREGA_CONCLUIDA.
     * A nota de agilidade vem do tempo entre coleta e entrega; a nota de
     * qualidade, por enquanto, replica a mesma nota calculada (é uma
     * simplificação documentada — o ideal é qualidade vir de uma pesquisa de
     * satisfação real com o cliente, o que fica como próximo passo).
     */
    public static Avaliacao registrarAutomatica(ColetaProjetada coleta, int notaCalculada) {
        return new Avaliacao(AvaliacaoId.novo(), coleta.getId(), coleta.getTransportadoraId(),
                coleta.getCidade(), coleta.getUf(), notaCalculada, notaCalculada, OrigemAvaliacao.AUTOMATICA);
    }

    public AvaliacaoId getId() { return id; }
    public ColetaId getColetaId() { return coletaId; }
    public TransportadoraId getTransportadoraId() { return transportadoraId; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public int getNotaQualidade() { return notaQualidade; }
    public int getNotaAgilidade() { return notaAgilidade; }
    public OrigemAvaliacao getOrigem() { return origem; }
    public OffsetDateTime getCriadaEm() { return criadaEm; }
    public OffsetDateTime getCanceladaEm() { return canceladaEm; }
}



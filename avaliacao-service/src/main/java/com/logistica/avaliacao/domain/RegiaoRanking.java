package com.logistica.avaliacao.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("regiao_ranking")
public class RegiaoRanking {

    @Id
    @Column("id")
    private Long id;

    @Getter
    @Column("transportadora_id")
    private TransportadoraId transportadoraId;

    @Getter
    @Column("cidade")
    private String cidade;

    @Getter
    @Column("uf")
    private String uf;

    @Column("soma_qualidade")
    private long somaQualidade;

    @Column("soma_agilidade")
    private long somaAgilidade;

    @Getter
    @Column("contagem")
    private long contagem;

    protected RegiaoRanking() {
    }

    public double getNotaQualidadeMedia() {
        return contagem == 0 ? 0 : Math.round((somaQualidade / (double) contagem) * 100.0) / 100.0;
    }

    public double getNotaAgilidadeMedia() {
        return contagem == 0 ? 0 : Math.round((somaAgilidade / (double) contagem) * 100.0) / 100.0;
    }
}

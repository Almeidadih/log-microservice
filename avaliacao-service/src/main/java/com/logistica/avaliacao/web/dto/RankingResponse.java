package com.logistica.avaliacao.web.dto;

import com.logistica.avaliacao.domain.RegiaoRanking;

import java.util.UUID;

public record RankingResponse(
        UUID transportadoraId ,
        String cidade ,
        String uf ,
        double notaQualidadeMedia ,
        double notaAgilidadeMedia ,
        long totalAvaliacoes
) {

    public static RankingResponse de (RegiaoRanking ranking){
        return new RankingResponse(
                ranking.getTransportadoraId().value(),
                ranking.getCidade(),
                ranking.getUf(),
                ranking.getNotaQualidadeMedia(),
                ranking.getNotaAgilidadeMedia(),
                ranking.getContagem()
        );

    }
}

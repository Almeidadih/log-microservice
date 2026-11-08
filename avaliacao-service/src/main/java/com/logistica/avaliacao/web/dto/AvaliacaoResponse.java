package com.logistica.avaliacao.web.dto;

import com.logistica.avaliacao.domain.Avaliacao;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AvaliacaoResponse(
        UUID id,
        UUID coletaId,
        UUID transportadoraId,
        String cidade ,
        String uf,
        int notaQualidade ,
        int notaAgilidade ,
        OffsetDateTime criadaEm
) {

    public static  AvaliacaoResponse de (Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId().value(),
                avaliacao.getColetaId().value(),
                avaliacao.getTransportadoraId().value(),
                avaliacao.getCidade(),
                avaliacao.getUf(),
                avaliacao.getNotaQualidade(),
                avaliacao.getNotaAgilidade(),
                avaliacao.getCriadaEm()
        );
    }
}

package com.logistica.fiscal.web.dto;

import com.logistica.fiscal.domain.NotaFiscal;
import com.logistica.fiscal.domain.StatusNotaFiscal;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record NotaFiscalResponse(
        UUID id,
        UUID customerId,
        UUID transportadoraId,
        String numero,
        BigDecimal valor,
        StatusNotaFiscal status,
        OffsetDateTime criadaEm,
        OffsetDateTime atualizadaEm
) {
    public static NotaFiscalResponse de(NotaFiscal notaFiscal) {
        return new NotaFiscalResponse(
                notaFiscal.getId().value(),
                notaFiscal.getCustomerId().value(),
                notaFiscal.getTransportadoraId() != null ? notaFiscal.getTransportadoraId().value() : null,
                notaFiscal.getNumero(),
                notaFiscal.getValor(),
                notaFiscal.getStatus(),
                notaFiscal.getCriadaEm(),
                notaFiscal.getAtualizadaEm()
        );
    }
}

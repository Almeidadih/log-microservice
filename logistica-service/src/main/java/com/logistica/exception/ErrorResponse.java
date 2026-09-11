package com.logistica.exception;

import java.time.OffsetDateTime;

public record ErrorResponse(
        String codigo,
        String mensagem,
        OffsetDateTime timestamp
) {

    public static ErrorResponse de(String codigo, String mensagem) {
        return new ErrorResponse(codigo, mensagem, OffsetDateTime.now());
    }
}

package com.logistica.exception;

import com.logistica.domain.ColetaId;

public class EntregaInvalidaException extends RuntimeException {
    public EntregaInvalidaException(ColetaId id) {
        super("Coleta " + id + " já está marcada como entregue");
    }

    @Override
    public String getCodigo() {
        return "ENTREGA_INVALIDA";
    }
}

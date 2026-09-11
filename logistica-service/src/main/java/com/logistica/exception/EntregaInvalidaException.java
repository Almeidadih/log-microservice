package com.logistica.exception;

import com.logistica.domain.ColetaId;

public class EntregaInvalidaException extends DominioFrotaException{
    public EntregaInvalidaException(ColetaId id) {
        super("Coleta " + id + " já está marcada como entregue");
    }

    @Override
    public String getCodigo() {
        return "ENTREGA_INVALIDA";
    }
}

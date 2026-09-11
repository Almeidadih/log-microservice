package com.logistica.exception;

import com.logistica.domain.TransportadoraId;

public class TransportadoraNaoEncontradaException extends DominioFrotaException {
    public TransportadoraNaoEncontradaException(TransportadoraId id) {
        super("Transportadora não encontrada: " + id);
    }

    @Override
    public String getCodigo() {
        return "TRANSPORTADOR_NAO_ENCONTRADO";
    }
}

package com.logistica.exception;

import com.logistica.domain.MotoristaId;

public class MotoristaNaoEncontradoException extends DominioFrotaException {
    public MotoristaNaoEncontradoException(MotoristaId id) {
        super("Motorista não encontrado: " + id);
    }

    @Override
    public String getCodigo() {
        return "MOTORISTA_NAO_ENCONTRADO";
    }
}

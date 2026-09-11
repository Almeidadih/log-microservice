package com.logistica.exception;

import com.logistica.domain.ColetaId;

public class ColetaNaoEncontradaException extends DominioFrotaException {
    public ColetaNaoEncontradaException(ColetaId id) {
        super("Coleta nao encontrada: " + id);
    }
    @Override
    public String getCodigo() {
        return "COLETA_NAO_ENCONTRADA";
    }
}

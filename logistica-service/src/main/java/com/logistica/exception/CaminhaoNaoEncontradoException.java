package com.logistica.exception;

import com.logistica.domain.CaminhaoId;

public class CaminhaoNaoEncontradoException extends DominioFrotaException{
    public CaminhaoNaoEncontradoException(CaminhaoId id) {
        super("Caminhão não encontrado: " + id);
    }

    @Override
    public String getCodigo() {
        return "CAMINHAO_NAO+ENCONTRADO";
    }
}

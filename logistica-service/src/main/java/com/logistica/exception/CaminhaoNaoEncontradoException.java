package com.logistica.exception;

import com.logistica.domain.CaminhaoId;

public class CaminhaoNaoEncontradoException extends RuntimeException {
    public CaminhaoNaoEncontradoException(CaminhaoId message) {
        super(message);
    }
}

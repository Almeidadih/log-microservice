package com.logistica.exception;

public class CaminhaoNaoEncontradoException extends RuntimeException {
    public CaminhaoNaoEncontradoException(String message) {
        super(message);
    }
}

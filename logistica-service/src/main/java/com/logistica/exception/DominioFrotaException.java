package com.logistica.exception;

public class DominioFrotaException extends RuntimeException {
    protected DominioFrotaException(String message) {
        super(message);
    }

    public String getCodigo() {
        return null;
    }
}

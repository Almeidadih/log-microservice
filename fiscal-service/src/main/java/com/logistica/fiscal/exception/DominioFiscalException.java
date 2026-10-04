package com.logistica.fiscal.exception;

public abstract class DominioFiscalException extends RuntimeException {

    protected DominioFiscalException(String message) {
        super(message);
    }
    public abstract String getCodigo();
}

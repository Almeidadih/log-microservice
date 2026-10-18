package com.logistica.cepservice.exception;

public abstract class DominioCepException extends RuntimeException {
    protected DominioCepException(String message) {
        super(message);
    }
    public abstract String getCodigo();
}

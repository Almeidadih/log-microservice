package com.logistica.avaliacao.exception;

public abstract class DominioAvaliacaoException extends RuntimeException {
    public DominioAvaliacaoException(String message) {
        super(message);
    }

    public abstract String getCodigo();
}

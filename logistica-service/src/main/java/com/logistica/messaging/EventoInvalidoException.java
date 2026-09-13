package com.logistica.messaging;

public class EventoInvalidoException extends RuntimeException {
    public EventoInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

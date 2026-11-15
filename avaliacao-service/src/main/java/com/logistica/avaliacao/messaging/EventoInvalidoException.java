package com.logistica.avaliacao.messaging;

public class EventoInvalidoException extends RuntimeException{
    public EventoInvalidoException(String message , Throwable cause){
        super(message, cause);
    }
}

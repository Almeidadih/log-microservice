package com.logistica.fiscal.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =  LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotaFiscalNaoEncontradException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNaoEncontrada(NotaFiscalNaoEncontradException ex){
        log.warn("Nota fiscal não encontrada: {} " ,  ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.de(ex.getCodigo(),  ex.getMessage())));
    }

}

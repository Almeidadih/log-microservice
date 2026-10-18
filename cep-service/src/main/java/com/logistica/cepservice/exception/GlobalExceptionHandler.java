package com.logistica.cepservice.exception;

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

    @ExceptionHandler(CepInvalidoException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCepInvalido(CepInvalidoException ex) {
        log.warn("CEP inválido: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.de(ex.getCodigo(),  ex.getMessage())));
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCepNaoEncontrado(CepNaoEncontradoException ex) {
        log.warn("CEP não encontrado: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.de(ex.getCodigo(),  ex.getMessage())));
    }

    @ExceptionHandler(DominioCepException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDominio(DominioCepException ex) {
        log.warn("Erro de dominio [{}]: {}", ex.getCodigo(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.de(ex.getCodigo(),  ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenerico(Exception ex) {
        log.error("Erro inesperado: {}" , ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.de("ERRO_INESPERADO", "Ocorreu um erro inesperado")));
    }

}

package com.logistica.fiscal.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
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

    @ExceptionHandler(StatusInvalidoException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleStatusInvalido(StatusInvalidoException ex){
        log.warn("Transição de status invalido: {} " ,  ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.de(ex.getCodigo(),  ex.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidacao(WebExchangeBindException ex){
        String mensagem = ex.getFieldErrors().stream()
                .map(erro -> erro.getField() + "; " + erro.getDefaultMessage())
                .reduce((a,b) -> a + "; " + b)
                .orElse( "Dados Inválidos");
        log.warn("Erro de validação: {} ", mensagem);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.de("VALIDACAO_INVALIDA", mensagem)));
    }
    @ExceptionHandler(DominioFiscalException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDominio(DominioFiscalException ex) {
        log.warn("Erro de domínio [{}]: {}", ex.getCodigo(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.de(ex.getCodigo(), ex.getMessage())));
    }
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenerico(Exception ex) {
        log.error("Erro inesperado", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.de("ERRO_INESPERADO", "Ocorreu um erro inesperado")));
    }

}

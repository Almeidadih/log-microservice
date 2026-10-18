package com.logistica.cepservice.exception;

public class CepInvalidoException extends DominioCepException {
    public CepInvalidoException(String valorRecebido) {
        super("CEP inválido: " + valorRecebido);
    }

    @Override
    public String getCodigo() {
        return "CEP_INVALIDO";
    }
}

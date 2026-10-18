package com.logistica.cepservice.exception;

import com.logistica.cepservice.domain.Cep;

public class CepNaoEncontradoException extends DominioCepException {
    public CepNaoEncontradoException(Cep cep) {
        super("CEP não encontrado na base: " + cep.formatado());
    }

    @Override
    public String getCodigo() {
        return "CEP_NAO_ENCNONTRADO";
    }
}

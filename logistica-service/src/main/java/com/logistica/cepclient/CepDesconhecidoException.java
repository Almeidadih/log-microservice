package com.logistica.cepclient;

import com.logistica.exception.DominioFrotaException;

public class CepDesconhecidoException extends DominioFrotaException {
    protected CepDesconhecidoException(String cep) {
        super("cep-service não reconhece o CEP: " + cep);
    }

    @Override
    public String getCodigo() {
        return super.getCodigo();
    }
}

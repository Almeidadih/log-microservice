package com.logistica.fiscal.exception;

import com.logistica.fiscal.domain.NotaFiscalId;
import com.logistica.fiscal.domain.StatusNotaFiscal;

public class StatusInvalidoException extends DominioFiscalException {

    public StatusInvalidoException(NotaFiscalId id, StatusNotaFiscal statusAtual, StatusNotaFiscal statusDesejado) {
        super("Nota fiscal %s não pode sair de %s para %s".formatted(id, statusAtual, statusDesejado));
    }

    @Override
    public String getCodigo() {
        return "";
    }
}

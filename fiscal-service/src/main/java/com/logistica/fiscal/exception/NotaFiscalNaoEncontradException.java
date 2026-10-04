package com.logistica.fiscal.exception;

import com.logistica.fiscal.domain.NotaFiscalId;

public class NotaFiscalNaoEncontradException extends DominioFiscalException {


    public NotaFiscalNaoEncontradException(NotaFiscalId id) {
        super("Nota fiscal não encontrada: " + id);
    }

    @Override
    public String getCodigo() {
        return "NOTA_FISCAL_NAO_ENCONTRADA";
    }
}

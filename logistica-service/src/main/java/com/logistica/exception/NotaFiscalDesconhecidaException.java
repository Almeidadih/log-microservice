package com.logistica.exception;

import com.logistica.domain.NotaFiscalId;

/**
 * Lançada quando tentam registrar uma coleta para uma nota fiscal que este
 * serviço ainda não "viu" via evento do Kafka (a projeção local não tem esse
 * registro). Normalmente indica uma condição de corrida: a coleta foi
 * solicitada antes do evento NOTA_FISCAL_REGISTRADA chegar.
 */

public class NotaFiscalDesconhecidaException extends DominioFrotaException{
    public NotaFiscalDesconhecidaException(NotaFiscalId id) {
        super("Nota fiscal ainda não reconhecida por este serviço: " + id);
    }

    @Override
    public String getCodigo() {
        return "NOTA_FISCAL_DESCONHECIDA";
    }
}

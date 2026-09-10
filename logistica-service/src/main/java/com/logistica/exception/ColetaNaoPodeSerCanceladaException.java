package com.logistica.exception;

import com.logistica.domain.ColetaId;

/**
 * Lançada quando se tenta cancelar uma coleta que já foi entregue. Isso não
 * é um bug nem uma falha técnica — é um LIMITE REAL de compensação: o
 * software não tem como desfazer um caminhão que já saiu e já entregou a
 * mercadoria. Quando isso acontece, a compensação automática para por aqui
 * e vira um caso que precisa de intervenção humana (estorno, nova coleta de
 * devolução, etc) — não é algo que o sistema resolve sozinho.
 */
public class ColetaNaoPodeSerCanceladaException extends RuntimeException {
    public ColetaNaoPodeSerCanceladaException(ColetaId id) {
        super("Coleta " + id + " já foi entregue e não pode mais ser cancelada automaticamente");
    }
    @Override
    public String getCodigo() {
        return "COLETA_NAO_PODE_SER_CANCELADA";
    }


}

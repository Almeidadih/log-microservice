package com.logistica.avaliacao.exception;


import com.logistica.avaliacao.domain.ColetaId;

/**
 * Lançada quando chega uma avaliação para uma coleta que este serviço ainda
 * não "viu" via evento do Kafka — mesma ideia do NotaFiscalDesconhecidaException
 * no logistica-service: a projeção local ainda não tem esse registro.
 */
public class ColetaDesconhecidaException extends DominioAvaliacaoException {
    public ColetaDesconhecidaException(ColetaId coletaId) {
        super("Coleta ainda não reconhecida por este serviço: " + coletaId);
    }

    @Override
    public String getCodigo() {
        return "COLETA_DESCONHECIDA";
    }
}

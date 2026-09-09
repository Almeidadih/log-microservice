package com.logistica.domain;

public enum StatusColeta {
    COLETADA,
    ENTREGUE,
    // Estado que resulta da compensação disparada por um
    // NOTA_FISCAL_STATUS_ALTERADO(CANCELADA) vindo do fiscal-service. Só
    // alcançável a partir de COLETADA — uma coleta já ENTREGUE não pode
    // virar CANCELADA (ver Coleta.cancelar()).
    CANCELADA
}

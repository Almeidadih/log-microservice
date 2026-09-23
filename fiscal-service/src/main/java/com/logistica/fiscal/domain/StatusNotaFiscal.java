package com.logistica.fiscal.domain;



import java.util.Set;

public enum StatusNotaFiscal {

    PENDENTE(Set.of()),
    CONFERIDA(Set.of("PENDENTE")),
    NAO_CONFERIDA(Set.of("PENDENTE")),
    DEVOLUCAO(Set.of("CONFERIDA", "NAO_CONFERIDA")),
    // Cancelamento so' e' permitido antes da nota chegar num estado
    // terminal (DEVOLUCAO). E' esse status que dispara a compensacao no
    // logistica-service (ver NotaFiscalEventConsumer la').
    CANCELADA(Set.of("PENDENTE", "CONFERIDA", "NAO_CONFERIDA"));

    private final Set<String> origensPermitidas;

    StatusNotaFiscal(Set<String> origensPermitidas) {
        this.origensPermitidas = origensPermitidas;
    }

    /**
     * Valida se a transição do status atual para este status é permitida,
     * refletindo o ciclo de vida real da nota fiscal.
     */
    public boolean podeTransicionarDe(StatusNotaFiscal origem) {
        if (origem == null) {
            return this == PENDENTE;
        }
        return origensPermitidas.contains(origem.name());
    }

}

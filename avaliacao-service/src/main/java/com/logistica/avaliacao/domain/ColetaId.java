package com.logistica.avaliacao.domain;

import java.util.UUID;

/** Copia local, propria deste servico (ver explicacao dos IDs tipados nos outros servicos). */
public record ColetaId(UUID value) {

    public static ColetaId de(String texto) {
        return new ColetaId(UUID.fromString(texto));
    }
    @Override public String toString() {
        return value.toString();
    }
}

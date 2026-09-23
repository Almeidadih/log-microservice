package com.logistica.fiscal.domain;

import java.util.UUID;

/**
 * Identificador tipado da transportadora responsável pela coleta/entrega.
 * Usado depois no avaliacao-service para ranquear qualidade e agilidade por região.
 */
public record TransportadoraId(UUID value) {

    public static TransportadoraId de(String texto) {
        return new TransportadoraId(UUID.fromString(texto));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

package com.logistica.domain;

import java.util.UUID;

public record ColetaId(UUID value) {
    public static ColetaId novo() {
        return new ColetaId(UUID.randomUUID());
    }
    public static ColetaId de(String texto) {
        return new ColetaId(UUID.fromString(texto));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

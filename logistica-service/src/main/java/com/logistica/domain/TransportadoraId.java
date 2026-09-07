package com.logistica.domain;

import java.util.UUID;

public record TransportadoraId(UUID value) {

    public static TransportadoraId novo(){
        return new TransportadoraId(UUID.randomUUID());
    }

    public static TransportadoraId de(String texto){
        return new TransportadoraId(UUID.fromString(texto));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

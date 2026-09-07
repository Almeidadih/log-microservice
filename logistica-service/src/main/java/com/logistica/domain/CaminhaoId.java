package com.logistica.domain;

import java.util.UUID;

public record CaminhaoId (UUID value) {

    public static CaminhaoId novo() {
        return new CaminhaoId(UUID.randomUUID());
    }

    public static  CaminhaoId de(String texto){
        return new CaminhaoId(UUID.fromString(texto));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

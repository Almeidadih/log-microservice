package com.logistica.avaliacao.domain;

import java.util.UUID;

public record AvaliacaoId(UUID value) {
    public static AvaliacaoId novo() {
        return new AvaliacaoId(UUID.randomUUID());
    }
    @Override
    public String toString() {
        return value.toString();
    }
}

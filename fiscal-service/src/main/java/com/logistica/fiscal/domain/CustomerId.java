package com.logistica.fiscal.domain;

import java.util.UUID;

/**
 * Identificador tipado do cliente dono da mercadoria.
 * Reaproveitado em outros microsserviços (logistica-service, avaliacao-service)
 * para sabermos de onde a rastreabilidade se origina.
 */
public record  CustomerId(UUID value) {

    public static CustomerId de(String texto) {
        return new CustomerId(UUID.fromString(texto));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

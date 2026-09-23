package com.logistica.fiscal.domain;

import java.util.UUID;

/**
 * Identificador tipado da Nota Fiscal.
 * Evita trocar por engano um NotaFiscalId por um CustomerId ou TransportadoraId
 * em assinaturas de método, e viaja como correlation id nos eventos do Kafka,
 * permitindo rastrear a nota fiscal em todos os microsserviços.
 */
public record NotaFiscalId(UUID value) {

    public static NotaFiscalId novo() {
        return new NotaFiscalId(UUID.randomUUID());
    }
    public static NotaFiscalId de(String texto) {
        return new NotaFiscalId(UUID.fromString(texto));
    }

    @Override

    public String toString() {
        return value.toString();
    }
}

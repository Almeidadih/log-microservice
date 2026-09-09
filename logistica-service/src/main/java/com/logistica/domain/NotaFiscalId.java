package com.logistica.domain;

import java.util.UUID;

/**
 * Copia local do identificador de nota fiscal.
 *
 * Importante: este NAO e o mesmo Java type do fiscal-service - cada
 * microsservico define seus proprios tipos, mesmo que representem o mesmo
 * conceito de negocio. Nao compartilhamos classes de dominio entre servicos
 * (isso criaria acoplamento forte via uma biblioteca compartilhada). O que os
 * une e o valor do UUID, que viaja dentro dos eventos do Kafka.
 */
public record NotaFiscalId(UUID value) {
    public static NotaFiscalId de(String texto){
        return new NotaFiscalId(UUID.fromString(texto));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

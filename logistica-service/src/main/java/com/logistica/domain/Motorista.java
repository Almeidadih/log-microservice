package com.logistica.domain;

import org.springframework.data.relational.core.mapping.Table;

@Table("motorista")
public class Motorista {

    private MotoristaId id;

    private String nome;

    private String cnh;

    private TransportadoraId transportadoraId;

    protected Motorista() {
    }

    private Motorista(MotoristaId id, String nome, String cnh, TransportadoraId transportadoraId) {
        this.id = id;
        this.nome = nome;
        this.cnh = cnh;
        this.transportadoraId = transportadoraId;
    }
    public static Motorista registrar(String nome, String cnh, TransportadoraId transportadoraId) {
        return new Motorista(MotoristaId.novo(), nome, cnh, transportadoraId);
    }

    public MotoristaId getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnh() {
        return cnh;
    }

    public TransportadoraId getTransportadoraId() {
        return transportadoraId;
    }
}

package com.logistica.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("caminhao")
public class Caminhao {
    @Id
    @Column("id")
    private CaminhaoId id;

    @Column("placa")
    private String placa;
    @Column("modelo")
    private String modelo;

    @Column("transportadora_id")
    private TransportadoraId transportadoraId;

    protected Caminhao() {
    }

    private Caminhao(CaminhaoId id, String placa, String modelo, TransportadoraId transportadoraId) {
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.transportadoraId = transportadoraId;
    }

    public static Caminhao registrar(String placa, String modelo, TransportadoraId transportadoraId) {
        return  new Caminhao(CaminhaoId.novo(),placa,modelo,transportadoraId);
    }

    public CaminhaoId getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public TransportadoraId getTransportadoraId() {
        return transportadoraId;
    }
}

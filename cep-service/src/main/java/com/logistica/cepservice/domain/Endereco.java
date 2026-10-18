package com.logistica.cepservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "endereco")
public class Endereco {

    @Id
    @Column("cep")
    private Cep cep;

    @Column("logradouro")
    private String logradouro;

    @Column("bairro")
    private String bairro;

    @Column("cidade")
    private String cidade;

    @Column("uf")
    private String uf;

    @Column("latitude")
    private double latitude;

    @Column("longitude")
    private double longitude;

    protected Endereco() {
    }

    public Endereco(Cep cep, String logradouro, String bairro, String cidade, String uf,
                    double latitude, double longitude) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Cep getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }



}

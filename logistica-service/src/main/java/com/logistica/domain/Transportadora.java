package com.logistica.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transportadora")
public class Transportadora {

    @Id
    @Column("id")
    private TransportadoraId id;

    @Column("nome")
    private String nome;
    @Column("cnpj")
    private String cnpj;

    protected  Transportadora() {
    }
    public Transportadora(TransportadoraId id, String nome, String cnpj) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
    }

    private void registrar(TransportadoraId id, String nome, String cnpj) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
    }

    public static Transportadora registrar(){
        return new Transportadora(TransportadoraId.novo(), registrar().nome, registrar().cnpj);
    }

    public TransportadoraId getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }
}

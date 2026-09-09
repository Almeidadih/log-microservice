package com.logistica.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


/**
 * Cópia local (read model) dos dados mínimos da nota fiscal que o
 * logistica-service precisa conhecer: se ela existe e qual o status atual.
 *
 * Este serviço NÃO é dono dessa informação — quem manda a "verdade" é o
 * fiscal-service. Esta tabela é só uma projeção, populada de forma assíncrona
 * pelos eventos do Kafka (NotaFiscalEventConsumer). Isso evita que o
 * logistica-service precise chamar o fiscal-service via HTTP toda vez que
 * precisar validar se uma nota fiscal existe antes de registrar uma coleta.
 */
@Table("nota_fiscal_registrada")
public class NotaFiscalRegistrada {

    @Id
    @Column("id")
    private NotaFiscalId id;

    @Column("status")
    private String status;

    protected NotaFiscalRegistrada() {
    }

    public NotaFiscalRegistrada(NotaFiscalId id, String status) {
        this.id = id;
        this.status = status;
    }

    public NotaFiscalId getId() { return id; }
    public String getStatus() { return status; }
}

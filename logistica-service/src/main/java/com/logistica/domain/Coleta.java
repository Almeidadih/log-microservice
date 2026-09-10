package com.logistica.domain;

import com.logistica.exception.EntregaInvalidaException;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("coleta")
public class Coleta {

    @Id
    @Column("id")
    private ColetaId id;

    @Column("nota_fiscal_id")
    private NotaFiscalId notaFiscalId;

    @Column("transportadora_id")
    private TransportadoraId transportadoraId;

    @Column("motorista_id")
    private MotoristaId motoristaId;

    @Column("caminhao_id")
    private CaminhaoId caminhaoId;

    @Column("cep_origem")
    private String cepOrigem;

    @Column("cidade")
    private String cidade;

    @Column("uf")
    private String uf;

    @Column("status")
    private StatusColeta status;

    @Column("coletada_em")
    private OffsetDateTime coletadaEm;

    @Column("entregue_em")
    private OffsetDateTime entregueEm;

    protected Coleta() {
    }

    private Coleta(ColetaId id, NotaFiscalId notaFiscalId, TransportadoraId transportadoraId,
                   MotoristaId motoristaId, CaminhaoId caminhaoId, String cepOrigem, String cidade, String uf) {
        this.id = id;
        this.notaFiscalId = notaFiscalId;
        this.transportadoraId = transportadoraId;
        this.motoristaId = motoristaId;
        this.caminhaoId = caminhaoId;
        this.cepOrigem = cepOrigem;
        this.cidade = cidade;
        this.uf = uf;
        this.status = StatusColeta.COLETADA;
        this.coletadaEm = OffsetDateTime.now();
    }

    /**
     * cidade/uf já vêm resolvidos (via cep-service) no momento da criação —
     * a Coleta nasce sabendo sua região, não precisa ser consultada de novo
     * depois para descobrir isso.
     */
    public static Coleta registrar(NotaFiscalId notaFiscalId, TransportadoraId transportadoraId,
                                   MotoristaId motoristaId, CaminhaoId caminhaoId,
                                   String cepOrigem, String cidade, String uf) {
        return new Coleta(ColetaId.novo(), notaFiscalId, transportadoraId, motoristaId, caminhaoId,
                cepOrigem, cidade, uf);
    }

    /**
     * Marca a coleta como entregue. Só pode acontecer uma vez — uma coleta
     * já ENTREGUE não pode ser "reentregue" (é o mesmo espírito da máquina
     * de estados da nota fiscal no fiscal-service, só que bem mais simples:
     * só dois estados aqui).
     */
    public void marcarComoEntregue() {
        if (status == StatusColeta.ENTREGUE) {
            throw new EntregaInvalidaException(id);
        }
        this.status = StatusColeta.ENTREGUE;
        this.entregueEm = OffsetDateTime.now();
    }

    /**
     * Aplica a compensação: cancela a coleta em reação ao cancelamento da
     * nota fiscal correspondente. Só é possível se a coleta ainda não foi
     * entregue — uma vez que a mercadoria já saiu de verdade, não existe
     * "desfazer" isso automaticamente (ver Javadoc de
     * ColetaNaoPodeSerCanceladaException para o porquê).
     */
    public void cancelar() {
        if (status == StatusColeta.ENTREGUE) {
            throw new com.logistica.exception.ColetaNaoPodeSerCanceladaException(id);
        }
        if (status == StatusColeta.CANCELADA) {
            return; // já cancelada — idempotente, não é erro reprocessar
        }
        this.status = StatusColeta.CANCELADA;
    }

    public ColetaId getId() {
        return id;
    }
    public NotaFiscalId getNotaFiscalId() {
        return notaFiscalId;
    }
    public TransportadoraId getTransportadoraId() {
        return transportadoraId;
    }
    public MotoristaId getMotoristaId() {
        return motoristaId;
    }
    public CaminhaoId getCaminhaoId() {
        return caminhaoId;
    }
    public String getCepOrigem() {
        return cepOrigem;
    }
    public String getCidade() {
        return cidade;
    }
    public String getUf() {
        return uf;
    }
    public StatusColeta getStatus() {
        return status;
    }
    public OffsetDateTime getColetadaEm() {
        return coletadaEm;
    }
    public OffsetDateTime getEntregueEm() {
        return entregueEm;
    }
}

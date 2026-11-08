package com.logistica.avaliacao.exception;


import com.logistica.avaliacao.domain.ColetaId;

/**
 * Lançada quando já existe uma avaliação para a coleta informada — o banco
 * é quem realmente garante isso (constraint UNIQUE em avaliacao.coleta_id);
 * esta exceção é só a tradução dessa violação para algo que a aplicação
 * sabe tratar de forma diferente dependendo de quem chamou:
 *
 *  - no fluxo MANUAL (AvaliacaoService, via API), isso vira um 409 para o
 *    cliente da API, avisando que a avaliação já existe;
 *  - no fluxo AUTOMATICO (EntregaProcessamentoListener, via RabbitMQ), isso
 *    é tratado como sucesso silencioso — significa que esta mensagem já foi
 *    processada antes (uma redelivery), então não há nada a fazer além de
 *    confirmar e seguir em frente.
 */
public class AvaliacaoDuplicadaException extends DominioAvaliacaoException {

    public AvaliacaoDuplicadaException(ColetaId coletaId) {
        super("Já existe uma avaliação registrada para a coleta: " + coletaId);
    }

    @Override
    public String getCodigo() {
        return "AVALIACAO_DUPLICADA";
    }
}

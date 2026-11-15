package com.logistica.avaliacao.messaging;
/**
 * Espelha o payload JSON publicado pelo logistica-service no tópico
 * logistica.cancelamento.eventos, quando uma coleta é cancelada em reação
 * ao cancelamento da nota fiscal correspondente.
 */
public record CancelamentoEventPayload(
        String coletaId ,
        String notaFiscalId,
        String transportadoraId
) {
}

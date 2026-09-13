package com.logistica.messaging;

/**
 * Espelha o payload JSON publicado pelo fiscal-service:
 * {"notaFiscalId":"...","customerId":"...","status":"..."}
 *
 * Repare que isto é só um DTO de mensageria — não é a mesma classe Java que o
 * fiscal-service usa internamente. Cada serviço tem sua própria representação
 * do que recebe/envia; o contrato entre eles é o formato JSON, não código
 * compartilhado.
 */
public record NotaFiscalEventPayload(
        String notaFiscalId,
        String customerId,
        String status
) {
}

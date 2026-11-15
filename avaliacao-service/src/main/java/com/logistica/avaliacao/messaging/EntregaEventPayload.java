package com.logistica.avaliacao.messaging;


/**
 * Espelha o payload JSON publicado pelo logistica-service no tópico
 * logistica.entrega.eventos. Cópia local, própria deste serviço.
 */
public record EntregaEventPayload(
        String coletaId,
        String transportadoraId ,
        String cidade ,
        String uf ,
        String coletadaEm,
        String entregueEm
) {
}

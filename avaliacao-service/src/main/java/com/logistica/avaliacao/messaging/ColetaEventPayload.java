package com.logistica.avaliacao.messaging;


/**
 * Espelha o payload JSON publicado pelo logistica-service no tópico
 * logistica.coleta.eventos. Cópia local, própria deste serviço — mesmo
 * princípio já usado nos outros consumidores do projeto.
 */
public record ColetaEventPayload(
        String coletaId,
        String notaFiscalId,
        String transportadoraId ,
        String motoristaId ,
        String caminhaoId ,
        String cidade ,
        String uf,
        String coletadaEm
) {
}

package com.logistica.messaging;

import org.springframework.stereotype.Component;

/**
 * Consome o tópico "fiscal.nota-fiscal.eventos" publicado pelo fiscal-service
 * (via seu OutboxPublisher) e mantém a projeção local (NotaFiscalRegistrada)
 * atualizada.
 *
 * Fluxo: Kafka -> parse do JSON -> upsert na projeção -> commit manual do
 * offset. O commit só acontece DEPOIS que a gravação no Postgres teve sucesso
 * — se o processamento falhar, o offset não avança e a mensagem será
 * reentregue na próxima vez que o consumidor subir (at-least-once delivery).
 *
 * Além de manter a projeção, este consumidor é o PONTO DE ENTRADA da
 * compensação: quando o status recebido é CANCELADA, ele delega para
 * ColetaService.cancelarPorNotaFiscal() — é aqui que a "saga" reage ao
 * cancelamento de uma etapa anterior.
 */
@Component
public class NotaFiscalEventConsumer {
}

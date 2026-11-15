package com.logistica.avaliacao.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Declara as três filas usadas no processamento do evento ENTREGA_CONCLUIDA:
 *
 *  - PROCESSAMENTO: fila principal, onde o EntregaProcessamentoListener
 *    escuta de verdade.
 *  - RETRY: fila "de espera" — ninguém consome dela diretamente. Toda
 *    mensagem que cai aqui expira depois de TTL_RETRY_MS (x-message-ttl) e
 *    é automaticamente redirecionada (x-dead-letter-*) de volta para a fila
 *    de PROCESSAMENTO. É o truque clássico do RabbitMQ para simular
 *    "reprocessar esta mensagem daqui a N segundos", já que filas comuns
 *    não têm atraso nativo por mensagem.
 *  - MORTOS: fila de "estacionamento" para mensagens que já esgotaram o
 *    número máximo de tentativas — ficam aqui para inspeção manual em vez
 *    de serem descartadas silenciosamente.
 *
 * Usamos o exchange padrão (nome "") em toda publicação — publicar com
 * routing key igual ao nome da fila entrega direto nela, sem precisar
 * declarar Exchange/Binding próprios para este caso simples.
 */
@Configuration
public class RabbitMqConfig {


    public static final String FILA_PROCESSAMENTO = "avaliacao.entrega.processamento";
    public static final String FILA_RETRY = "avaliacao.entrega.retry";
    public static final String FILA_MORTOS = "avaliacao.entrega.mortos";
    public static final int TTL_RETRY_MS = 5000;
    public static final int MAX_TENTATIVAS = 5;
    public static final String HEADER_TENTATIVAS = "x-tentativas";

    @Bean
    public Queue filaProcessamento() {
        return QueueBuilder.durable(FILA_PROCESSAMENTO).build();
    }

    @Bean
    public Queue filaRetry() {
        return QueueBuilder.durable(FILA_RETRY)
                .withArgument("x-message-ttl", TTL_RETRY_MS)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", FILA_PROCESSAMENTO)
                .build();
    }

    @Bean
    public Queue filaMortos() {
        return QueueBuilder.durable(FILA_MORTOS).build();
    }
}

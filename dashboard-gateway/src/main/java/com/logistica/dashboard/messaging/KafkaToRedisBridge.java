package com.logistica.dashboard.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import java.time.OffsetDateTime;

/**
 * Consome os três tópicos do sistema (nota fiscal, coleta, ranking) e
 * republica cada mensagem, já embrulhada num EventoDashboard, num único
 * canal Redis. Por que passar pelo Redis em vez de ir direto do Kafka para
 * os WebSockets? Porque se este gateway rodar em mais de uma instância
 * (necessário para aguentar muitas conexões WebSocket simultâneas), só UMA
 * das instâncias vai efetivamente consumir cada mensagem do Kafka (elas
 * dividem as partições entre si, como qualquer consumer group) — mas TODAS
 * as instâncias têm clientes conectados que precisam saber do evento. O
 * Redis Pub/Sub broadcast a mensagem para todas as instâncias ouvindo o
 * canal (ver RedisEventBroadcaster), que aí sim entregam para os seus
 * próprios clientes WebSocket.
 */
@Component
public class KafkaToRedisBridge {

    private static final Logger log = LoggerFactory.getLogger(KafkaToRedisBridge.class);
    static final String CANAL_REDIS = "dashboard.eventos";

    private final KafkaReceiver<String, String> kafkaReceiver;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public KafkaToRedisBridge(KafkaReceiver<String, String> kafkaReceiver, ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.kafkaReceiver = kafkaReceiver;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void iniciarConsumo() {
        kafkaReceiver.receive()
                .concatMap(record -> processar(record.topic(), record.value())
                        .doOnSuccess(v -> record.receiverOffset().acknowledge())
                        .onErrorResume(erro -> {
                            log.error("Falha ao repassar evento do tópico {} para o Redis", record.topic(), erro);
                            return Mono.empty();
                        }))
                .subscribe();

        log.info("Ponte Kafka -> Redis iniciada");
    }

    private Mono<Void> processar(String topico, String payloadJson) {
        return Mono.fromCallable(() -> montarEnvelope(topico, payloadJson))
                .flatMap(envelope -> redisTemplate.convertAndSend(CANAL_REDIS, envelope))
                .doOnNext(quantosReceberam -> log.debug("Evento de {} repassado para {} instância(s) ouvindo o Redis",
                        topico, quantosReceberam))
                .then();
    }

    private String montarEnvelope(String topico, String payloadJson) throws Exception {
        JsonNode payload = objectMapper.readTree(payloadJson);
        EventoDashboard envelope = new EventoDashboard(topico, OffsetDateTime.now().toString(), payload);
        return objectMapper.writeValueAsString(envelope);
    }
}

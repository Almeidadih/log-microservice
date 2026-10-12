package com.logistica.dashboard.messaging;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


/**
 * Escuta o canal Redis compartilhado (dashboard.eventos) e republica cada
 * mensagem recebida num Sinks.Many local — é esse sink que cada sessão
 * WebSocket conectada nesta instância vai assinar (ver
 * DashboardWebSocketHandler).
 *
 * Um único listener Redis por instância, compartilhado entre N sessões
 * WebSocket: não abrimos uma subscription Redis nova por cliente conectado,
 * o que não escalaria bem com muitos clientes.
 */
@Component
public class RedisEventBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(RedisEventBroadcaster.class);

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    // onBackpressureBuffer: se um cliente lento não conseguir consumir no
    // mesmo ritmo, os eventos ficam em buffer para ele em vez de serem
    // descartados na hora ou travarem os outros clientes.
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    public RedisEventBroadcaster(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void iniciarEscuta() {
        redisTemplate.listenToChannel(KafkaToRedisBridge.CANAL_REDIS)
                .map(ReactiveSubscription.Message::getMessage)
                .doOnNext(mensagem -> {
                    Sinks.EmitResult resultado = sink.tryEmitNext(mensagem);
                    if (resultado.isFailure()) {
                        log.warn("Falha ao emitir evento para os clientes WebSocket: {}", resultado);
                    }
                })
                .doOnError(erro -> log.error("Erro na escuta do canal Redis", erro))
                .retry() // reconecta automaticamente se a assinatura Redis cair
                .subscribe();

        log.info("Escuta do canal Redis '{}' iniciada", KafkaToRedisBridge.CANAL_REDIS);
    }

    public Flux<String> eventos() {
        return sink.asFlux();
    }
}

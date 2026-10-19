package com.logistica.cepservice.service;

import com.logistica.cepservice.domain.Cep;
import com.logistica.cepservice.domain.Endereco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class CepCacheService {

    private static final Logger log = LoggerFactory.getLogger(CepCacheService.class);

    // CEP muda de dono raramente — um TTL de 24h é um bom equilíbrio entre
    // não bater sempre no banco e não servir dado desatualizado por meses.
    private static final Duration TTL = Duration.ofSeconds(24);

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public CepCacheService(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Endereco> buscar(Cep cep) {
        return redisTemplate.opsForValue().get(chave(cep))
                .cast(Endereco.class)
                .doOnNext(e -> log.debug("Cache HIT para o CEP {}", cep.formatado()));
    }

    public Mono<Boolean> salvar(Endereco endereco) {
        return redisTemplate.opsForValue()
                .set(chave(endereco.getCep()), endereco, TTL)
                .doOnNext(sucesso -> log.debug("CEP {} salvo no cache (TTL {}h)",
                        endereco.getCep().formatado(), TTL.toHours()));
    }

    private String chave(Cep cep){
        return "cep:" + cep.digitos();
    }
}

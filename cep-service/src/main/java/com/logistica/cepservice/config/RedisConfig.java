package com.logistica.cepservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.logistica.cepservice.domain.Endereco;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * O Redis aqui funciona como cache de leitura: em vez de bater no Postgres
 * (ou, futuramente, numa API externa de CEP) a cada requisição, guardamos o
 * Endereco já resolvido por um tempo (ver TTL no CepCacheService). A chave é
 * o próprio CEP (8 dígitos).
 */
@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Endereco> enderecoRedisTemplate(
            RedisConnectionFactory connectionFactory , ObjectMapper objectMapper) {

        Jackson2JsonRedisSerializer<Endereco> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Endereco.class);

        RedisSerializationContext<String, Endereco> context = RedisSerializationContext
                .newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}

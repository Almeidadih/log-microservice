package com.logistica.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import tools.jackson.databind.deser.jdk.StringDeserializer;

import java.util.Map;

/**
 * KafkaSender é o par do KafkaReceiver que já usamos para consumir: a
 * versão reativa (não bloqueante) de um producer Kafka, do reactor-kafka.
 * Mantemos os dois lados (consumo do fiscal-service, produção deste evento
 * de coleta) na mesma biblioteca, em vez de misturar reactor-kafka com
 * spring-kafka.
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaSender<String, String> kafkaSender() {
        Map<String, Object> props = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );
        return KafkaSender.create(SenderOptions.create(props));
    }
}

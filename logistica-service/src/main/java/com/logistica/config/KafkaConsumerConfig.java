package com.logistica.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import tools.jackson.databind.deser.jdk.StringDeserializer;

import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${^spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${logistica.kafka.topic-nota-fiscal}")
    private String topicoNotaFiscal;

    /**
     * KafkaReceiver é a versão reativa (não bloqueante) de um consumidor Kafka,
     * do projeto reactor-kafka. Diferente do @KafkaListener tradicional do
     * spring-kafka (que roda em uma thread dedicada e é essencialmente
     * bloqueante), o KafkaReceiver expõe o consumo como um Flux — encaixa
     * naturalmente no restante da stack WebFlux deste projeto.
     */
    @Bean
    public KafkaReceiver<Object, Object> notaFiscalKafkaReceiver() {
        Map<String,Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, "logistica-service" ,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false
        );

        ReceiverOptions<Object, Object> options = ReceiverOptions.create(props)
                .subscription(List.of(topicoNotaFiscal));

        return KafkaReceiver.create(options);
    }

}

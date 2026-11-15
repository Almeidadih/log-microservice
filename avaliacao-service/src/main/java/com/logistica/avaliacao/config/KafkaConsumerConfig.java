package com.logistica.avaliacao.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${avaliacao.kafka.topic-coleta}")
    private String topicoColeta;

    @Value("${avaliacao.kafka.topic-entrega}")
    private String topicoEntrega;

    @Value("${avaliacao.kafka.topic-cancelamento}")
    private String topicoCancelamento;

    @Bean
    public KafkaReceiver<String, String> coletaKafkaReceiver(){
        return criarReceiver("avaliacao-service", topicoColeta);
    }

    /**
     * Grupo de consumidor PRÓPRIO (diferente do coletaKafkaReceiver) — cada
     * KafkaReceiver aqui é uma conexão de consumidor independente; manter
     * grupos separados por tópico evita qualquer confusão na hora do
     * rebalanceamento de partições, já que o protocolo de consumer group do
     * Kafka pressupõe que todos os membros de um grupo assinam o mesmo
     * conjunto de tópicos.
     */
    @Bean
    public KafkaReceiver<String, String> entregaKafkaReceiver() {
        return criarReceiver("avaliacao-service-entrega", topicoEntrega);
    }

    @Bean
    public KafkaReceiver<String, String> cancelamentoKafkaReceiver() {
        return criarReceiver("avaliacao-service-cancelamento", topicoCancelamento);
    }

    private KafkaReceiver<String, String> criarReceiver(String groupId, String topico) {
        Map<String, Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false
        );

        ReceiverOptions<String, String> options = ReceiverOptions.<String, String>create(props)
                .subscription(List.of(topico));

        return KafkaReceiver.create(options);
    }
}

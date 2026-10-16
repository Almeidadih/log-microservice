package com.logistica.dashboard.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

/**
 * Um único KafkaReceiver, inscrito nos três tópicos do sistema. O gateway
 * não precisa de um consumidor por tópico — ele só repassa tudo adiante
 * (ver KafkaToRedisBridge), então um consumidor múltiplo-tópico é
 * suficiente e mais simples de operar.
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${dashboard.kafka.topic-nota-fiscal}")
    private String topicoNotaFiscal;

    @Value("${dashboard.kafka.topic-coleta}")
    private String topicoColeta;

    @Value("${dashboard.kafka.topic-ranking}")
    private String topicoRanking;

    @Bean
    public KafkaReceiver<String, String> dashboardKafkaReceiver() {
        Map<String, Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, "dashboard-gateway" ,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest ",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , false
        );

        ReceiverOptions<String , String> options =ReceiverOptions.<String, String>create(props)
                .subscription(List.of(topicoNotaFiscal, topicoColeta, topicoRanking));
//        ReceiverOptions<String, String> options = ReceiverOptions.create(props)
//                .subscription(List.of(topicoNotaFiscal, topicoColeta, topicoRanking));

        return KafkaReceiver.create(options);
    }

}

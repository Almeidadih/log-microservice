package com.logistica.dashboard.messaging;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Envelope comum para qualquer evento que chega ao dashboard, não importa de
 * qual tópico Kafka veio. O front-end usa o campo "topico" para decidir como
 * interpretar o "payload" (que continua sendo o JSON original publicado por
 * cada serviço, sem transformação — só encaixado aqui dentro).
 */
public record EventoDashboard(
        String topico,
        String recebidoEm ,
        JsonNode payload
) {
}

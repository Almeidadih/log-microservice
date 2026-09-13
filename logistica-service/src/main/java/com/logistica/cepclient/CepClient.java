package com.logistica.cepclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Ponte entre logistica-service e cep-service: uma chamada HTTP simples e
 * síncrona-do-ponto-de-vista-do-fluxo (mas não bloqueante — WebClient é
 * reativo) para descobrir a região (cidade/uf) de um CEP antes de registrar
 * a coleta.
 *
 * Por que HTTP direto em vez de outro evento Kafka aqui? Porque este é um
 * caso de "preciso da resposta AGORA para decidir o que fazer" — a coleta
 * só é registrada se o CEP for válido. Isso é diferente do fiscal-service
 * publicando "a nota fiscal mudou de status", onde ninguém está esperando a
 * resposta em tempo real. Comunicação síncrona (HTTP) para consultas que
 * bloqueiam uma decisão; eventos (Kafka) para notificar fatos que já
 * aconteceram. Os dois padrões convivem no mesmo sistema.
 */
@Component
public class CepClient {
    private static final Logger log = LoggerFactory.getLogger(CepClient.class);

    private final WebClient webClient;

    public CepClient(WebClient cepServiceWebClient) {
        this.webClient = cepServiceWebClient;
    }

    public Mono<EnderecoExterno> resolver(String cep) {
        return webClient.get()
                .uri("/api/v1/ceps/{cep}", cep)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new CepDesconhecidoException(cep)))
                .bodyToMono(EnderecoExterno.class)
                .doOnError(WebClientResponseException.class,
                        erro -> log.error("Erro ao consultar cep-service para o CEP {}: {}", cep, erro.getMessage()));
    }
}

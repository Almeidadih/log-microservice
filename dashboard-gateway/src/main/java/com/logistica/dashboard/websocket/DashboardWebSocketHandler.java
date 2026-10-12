package com.logistica.dashboard.websocket;

import com.logistica.dashboard.messaging.RedisEventBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Cada sessão WebSocket conectada assina o Flux compartilhado do
 * RedisEventBroadcaster — não existe um consumo "por cliente" do Kafka ou
 * do Redis, todos compartilham a mesma fonte. O handler só cuida de
 * transformar cada String (JSON) num WebSocketMessage e mandar pro cliente.
 *
 * Este gateway é "somente leitura" do ponto de vista do cliente: não
 * esperamos comandos vindos do WebSocket, só entregamos eventos. Por isso
 * não há lógica de recepção de mensagens aqui.
 */
@Component
public class DashboardWebSocketHandler implements WebSocketHandler {

    private static  final Logger log = LoggerFactory.getLogger(DashboardWebSocketHandler.class);

    private final RedisEventBroadcaster eventBroadcaster;

    public DashboardWebSocketHandler(RedisEventBroadcaster eventBroadcaster) {
        this.eventBroadcaster = eventBroadcaster;
    }


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("Cliente conectado ao dashboard: {}" , session.getId());

        Flux<String> mensagensRecebidas = session.receive()
                .map(msg -> msg.getPayloadAsText())
                .doOnNext(msg -> log.info("Recebimento recebido ao dashboard: {}" , session.getId(),msg));

        Flux<WebSocketMessage> saida = eventBroadcaster.eventos()
                .map(session::textMessage);

        return session.send(saida)
                .and(mensagensRecebidas)// consome (e descarta) o que o cliente mandar, sem travar a conexao
                .doFinally(sinalEncerramento -> log.info("Cliente desconectado do dashboard: {} ({})",
                        session.getId(), sinalEncerramento));
    }
}

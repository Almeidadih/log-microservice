package com.logistica.dashboard.config;

import com.logistica.dashboard.websocket.DashboardWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

/**
 * WebFlux não tem uma anotação tipo @WebSocketMapping — o registro é feito
 * "na mão", associando um path a um WebSocketHandler através de um
 * HandlerMapping. O WebSocketHandlerAdapter é a peça que faz o Spring
 * reconhecer esse handler como um destino válido de requisição.
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketHandlerMapping(DashboardWebSocketHandler  dashboardWebSocketHandler) {

        Map<String, WebSocketHandler> map = Map.of("/ws/dashboard", dashboardWebSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1); // precisa vir antes do mapeamento padrao de controllers REST
        return mapping;
    }
    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}

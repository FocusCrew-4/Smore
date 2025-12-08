package com.smore.auction.infrastructure.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 업그레이드 엔드포인트
        // http://localhost:6600/ws-auction
        registry.addEndpoint("/ws-auction")
            .setAllowedOriginPatterns("*")
            .withSockJS(); // 선택: SockJS fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버가 클라이언트에게 push 서버 -> 클라이언트
        registry.enableSimpleBroker("/sub");
        // 클라이언트가 서버로 보낼때 클라이언트 -> 서버
        registry.setApplicationDestinationPrefixes("/pub");
    }
}

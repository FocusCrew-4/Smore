package com.smore.auction.infrastructure.websocket.config;

import com.smore.auction.infrastructure.websocket.handler.AuctionHandshakeHandler;
import com.smore.auction.infrastructure.websocket.interceptor.AuctionHandshakeInterceptor;
import com.smore.auction.infrastructure.websocket.interceptor.AuctionStompInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuctionHandshakeInterceptor auctionHandshakeInterceptor;
    private final AuctionHandshakeHandler auctionHandshakeHandler;
    private final AuctionStompInterceptor auctionStompInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 업그레이드 엔드포인트
        // http://localhost:6600/ws-auction
        registry.addEndpoint("/ws-auction")
            .setAllowedOriginPatterns("*")
            .addInterceptors(auctionHandshakeInterceptor)
            .setHandshakeHandler(auctionHandshakeHandler)
            .withSockJS(); // 선택: SockJS fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버가 클라이언트에게 push 서버 -> 클라이언트
        registry.enableSimpleBroker("/sub");
        // 클라이언트가 서버로 보낼때 클라이언트 -> 서버
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(auctionStompInterceptor);
    }
}

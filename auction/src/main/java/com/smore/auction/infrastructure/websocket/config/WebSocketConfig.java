package com.smore.auction.infrastructure.websocket.config;

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

    private final AuctionStompInterceptor auctionStompInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 업그레이드 엔드포인트
        // http://localhost:6600/ws-auction
        registry.addEndpoint("/ws-auction")
            .setAllowedOriginPatterns("*")
            .addInterceptors(new AuctionHandshakeInterceptor())
            .withSockJS(); // 선택: SockJS fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버가 클라이언트에게 push 서버 -> 클라이언트
        registry.enableSimpleBroker("/sub");
        // 클라이언트가 서버로 보낼때 클라이언트 -> 서버
        registry.setApplicationDestinationPrefixes("/pub");
    }

    /*
    new ChannelInterceptor 를 직접 넣은 이유
    beforeHandshake 에서 넣어둔 attributes 읽어서 CONNECT 프레임에서 Principal 생성해서 accessor.setUser(...)로 등록
    간단한 사용자 식별자 등록 로직으로 외부 서비스 의존성도 없고 복잡한 주입도 필요없어 new 로 직접 등록
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(auctionStompInterceptor);
    }
}

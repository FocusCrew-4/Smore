package com.smore.auction.infrastructure.websocket.config;

import com.smore.auction.infrastructure.websocket.AuctionUserPrincipal;
import com.smore.auction.infrastructure.websocket.interceptor.AuctionHandshakeInterceptor;
import java.util.Objects;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
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
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend( Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (accessor.getCommand() == StompCommand.CONNECT) {
                    // attributes 의 타입이 Map<String, Object> 이기 때문에 Long 을 넣어도 컴파일러는 그 값을 Long 으로 저장
                    // 따라서 다시 꺼낼 때 반환 타입도 Object 이므로 Long 으로 사용하려면 명시적 다운캐스팅이 필수
                    Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
                    String role = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("role");

                    // Principal 등록
                    accessor.setUser(new AuctionUserPrincipal(userId, role));
                }

                return message;
            }
        });
    }
}

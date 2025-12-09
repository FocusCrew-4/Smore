package com.smore.auction.infrastructure.websocket.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.infrastructure.websocket.AuctionUserPrincipal;
import com.smore.auction.infrastructure.websocket.manager.AuctionPubManager;
import com.smore.auction.infrastructure.websocket.manager.AuctionSessionManager;
import java.security.Principal;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


// TODO: 공부해야함
@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionStompInterceptor implements ChannelInterceptor {

    private final AuctionSessionManager sessionManager;
    private final AuctionPubManager pubManager;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {

        log.info("preSend 진입하였습니다\n\n\n\n");
        stringRedisTemplate.opsForSet()
            .add("test","test");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 1. CONNECT 시점에 Principal 등록
        if (accessor.getCommand() == StompCommand.CONNECT) {
            log.info("principal 등록: 코넥트 이프문 동작\n\n\n\n");
            Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
            String role = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("role");
            accessor.setUser(new AuctionUserPrincipal(userId, role));
            return message;
        }

        Principal principal = accessor.getUser();
        String destination = accessor.getDestination();

        if (principal == null || destination == null) {
            log.info("null 방어코드 동작");
            log.info("destination null? {}", destination == null);
            log.info("principal null? {}", principal== null);
            return null; // 방어
        }
        log.info("principal: {}", principal.getName());
        log.info("destination: {}", destination);

        // 2. SUBSCRIBE 검증 (/sub/auction/** 만 허용)
        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            if (!destination.startsWith("/sub/auction/")) {
                return null; // 이상한 토픽 구독 차단
            }
            String auctionId = extractAuctionId(destination);
            sessionManager.handleSubscribe(
                accessor.getSessionId(),
                Long.valueOf(principal.getName()),
                auctionId
            );
        }

        // 3. SEND 검증 (/pub/auction/** 만 처리)
        if (accessor.getCommand() == StompCommand.SEND) {
            if (!destination.startsWith("/pub/auction/")) {
                return message; // 우리가 관리 안 하는 SEND 는 통과
            }

            String auctionId = extractAuctionId(destination);

            try {
                pubManager.validateSend(accessor.getSessionId(), auctionId);
            } catch (Exception e) {
                log.warn("Unauthorized SEND: session={}, auction={}", accessor.getSessionId(), auctionId);
                return null; // 메시지 브로커로 안 보냄
            }
        }

        return message;
    }

    private String extractAuctionId(String destination) {
        // "/sub/auction/{id}" 또는 "/pub/auction/{id}/..." 에서 {id} 파싱하는 로직 구현
        return "1";
    }
}
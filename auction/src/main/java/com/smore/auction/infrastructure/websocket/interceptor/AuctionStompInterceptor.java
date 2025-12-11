package com.smore.auction.infrastructure.websocket.interceptor;

import com.smore.auction.infrastructure.websocket.AuctionUserPrincipal;
import com.smore.auction.infrastructure.websocket.manager.AuctionPubManager;
import com.smore.auction.infrastructure.websocket.manager.AuctionSessionManager;
import java.security.Principal;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


// TODO: 공부 + 실제 경매 가능한 방만 Topic 생성해서 sub 하도록 수정 필요
@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionStompInterceptor implements ChannelInterceptor {

    private final AuctionSessionManager sessionManager;
    private final AuctionPubManager pubManager;

    // 정규식 문자열
    private static final Pattern AUCTION_ID_PATTERN =
        Pattern.compile("^/(sub|pub)/auction/([A-Za-z0-9-]+)");

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {

        log.info("preSend 진입하였습니다\n\n\n\n");
        log.info("message raw: {}", message);
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // 1. CONNECT 시점에 Principal 등록
        if (accessor.getCommand() == StompCommand.CONNECT) {
            log.info("principal 등록: 코넥트 동작\n\n\n\n");
            Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
            String role = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("role");
            accessor.setUser(new AuctionUserPrincipal(userId, role));
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        if (accessor.getCommand() == StompCommand.DISCONNECT) {
            log.info("disconnect 수신 및 동작 \n\n\n");
            sessionManager.handleDisconnect(accessor.getSessionId());
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
            log.info("sub 요청 감지");
            if (!destination.startsWith("/sub/auction/")) {
                log.info("이상한 sub 금지\n\n\n");
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
                log.info(String.valueOf(e));
                log.warn("Unauthorized SEND: session={}, auction={}", accessor.getSessionId(), auctionId);
                return null; // 메시지 브로커로 안 보냄
            }
        }

        return message;
    }

    // TODO: 정규식 활용 공부
    private String extractAuctionId(String destination) {
        // "/sub/auction/{id}" 또는 "/pub/auction/{id}/..." 에서 {id} 파싱하는 로직 구현
        Matcher m = AUCTION_ID_PATTERN.matcher(destination);
        if (m.find()) {
            return m.group(2); // UUID 부분만 반환
        }
        throw new IllegalArgumentException("Invalid destination: " + destination);
    }
}

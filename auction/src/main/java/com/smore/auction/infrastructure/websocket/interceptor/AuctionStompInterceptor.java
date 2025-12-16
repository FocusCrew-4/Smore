package com.smore.auction.infrastructure.websocket.interceptor;

import com.smore.auction.infrastructure.websocket.manager.AuctionPubManager;
import com.smore.auction.infrastructure.websocket.manager.AuctionSessionManager;
import java.security.Principal;
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
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


// TODO: 공부 + 실제 경매 가능한 방만 Topic 생성해서 sub 하도록 수정 필요
@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionStompInterceptor implements ChannelInterceptor {

    private final AuctionSessionManager sessionManager;
    private final AuctionPubManager pubManager;

    // 정규식 문자열 (SUB: /topic/auction/{id}, SEND: /pub/auction/{id})
    private static final Pattern SUBSCRIBE_AUCTION_PATTERN =
        Pattern.compile("^/topic/auction/([A-Za-z0-9-]+)");
    private static final Pattern SEND_AUCTION_PATTERN =
        Pattern.compile("^/pub/auction/([A-Za-z0-9-]+)");

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (command == StompCommand.DISCONNECT) {
            log.info("disconnect 수신 및 동작 \n\n\n");
            sessionManager.handleDisconnect(accessor.getSessionId());
            return message;
        }

        Principal principal = accessor.getUser();
        String destination = accessor.getDestination();

        // CONNECT, HEARTBEAT 등 destination 없는 프레임은 통과
        if (command == StompCommand.CONNECT || command == StompCommand.STOMP || command == null) {
            return message;
        }

        if (principal == null) {
            return null; // 인증 안 된 사용자 차단
        }

        // 2. SUBSCRIBE 검증 (/topic/auction/{id} 만 허용)
        if (command == StompCommand.SUBSCRIBE) {
            log.info("sub 요청 감지");
            if (destination == null) {
                return null;
            }
            if (!destination.startsWith("/topic/auction/")) {
                log.info("이상한 sub 금지\n\n\n");
                log.info(destination);
                return null; // 이상한 토픽 구독 차단
            }
            String auctionId = extractAuctionId(destination, true);
            sessionManager.handleSubscribe(
                accessor.getSessionId(),
                Long.valueOf(principal.getName()),
                auctionId
            );
        }

        // 3. SEND 검증 (/pub/auction/** 만 처리)
        if (command == StompCommand.SEND) {
            if (destination == null) {
                return null;
            }
            if (!destination.startsWith("/pub/auction/")) {
                return message; // 우리가 관리 안 하는 SEND 는 통과
            }

            String auctionId = extractAuctionId(destination, false);

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
    private String extractAuctionId(String destination, boolean isSubscribe) {
        Matcher matcher = isSubscribe
            ? SUBSCRIBE_AUCTION_PATTERN.matcher(destination)
            : SEND_AUCTION_PATTERN.matcher(destination);

        if (matcher.find()) {
            return matcher.group(1); // UUID 부분만 반환
        }
        throw new IllegalArgumentException("Invalid destination: " + destination);
    }
}

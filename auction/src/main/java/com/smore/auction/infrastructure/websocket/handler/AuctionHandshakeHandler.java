package com.smore.auction.infrastructure.websocket.handler;

import com.smore.auction.infrastructure.websocket.AuctionUserPrincipal;
import java.security.Principal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

// TODO: 확실하게 공부 해야함 여기서 principal 저장을 하고있음
@Component
@Slf4j
public class AuctionHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
        ServerHttpRequest request,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
        log.info("AuctionHandShakeHandler 진입하였습니다");
        Long userId = (Long) attributes.get("userId");
        String role = (String) attributes.get("role");

        if (userId == null || role == null) {
            log.warn("Handshake attributes missing. userId={}, role={}", userId, role);
            return null;
        }

        return new AuctionUserPrincipal(userId, role);
    }
}

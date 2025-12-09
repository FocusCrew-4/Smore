package com.smore.auction.infrastructure.websocket.interceptor;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Slf4j
public class AuctionHandshakeInterceptor implements HandshakeInterceptor {

    // TODO: 예외타입 통일 필요
    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes)
        throws Exception {

        var headers = request.getHeaders();
        String optionalUserId = headers.getFirst("X-USER-ID");
        if (optionalUserId == null) {
            throw new Exception("X-USER-ID header is missing");
        }
        String optionalRole = headers.getFirst("X-USER-ROLE");
        if (optionalRole == null) {
            throw new Exception("X-USER-ROLE header is missing");
        }
        // region socket 정상작동 확인시 활성화 필수
//        if (!optionalRole.equalsIgnoreCase("customer")) {
//            throw new Exception("Only customer roles are supported");
//        }
        // endregion

        attributes.put("USER_ID", Long.valueOf(optionalUserId));
        attributes.put("ROLE", optionalRole);
        return true;
    }

    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception) {
        if (exception != null) {
            log.error("Handshake failed: {}", exception.getMessage());
        } else {
            log.info("Handshake succeeded for request {}", request.getURI());
        }
    }
}

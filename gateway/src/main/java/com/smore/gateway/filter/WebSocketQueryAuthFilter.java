package com.smore.gateway.filter;

import com.smore.gateway.usecase.JwtValidation;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketQueryAuthFilter implements GlobalFilter, Ordered {

    private final JwtValidation jwtValidation;

    private static final String TOKEN_PARAM = "token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        log.info("ws 업그레이드 필터 진입");

        if (!isWebSocketOrSockJsRequest(request)) {
            return chain.filter(exchange);
        }

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && !authorization.isBlank()) {
            return chain.filter(exchange);
        }

        String token = request.getQueryParams().getFirst(TOKEN_PARAM);
        if (token == null || token.isBlank()) {
            return chain.filter(exchange);
        }

        log.info("ws 헤더값 추가 시작");
        String bearerToken = token.startsWith("Bearer ")
            ? token
            : "Bearer " + token;

        Jwt jwt =
            jwtValidation.validateJwt(bearerToken.replace("Bearer ", ""));

//        log.debug("role 값 머임: {}", jwt.getClaimAsString("role"));

        ServerHttpRequest mutated = request.mutate()
            .headers(h -> {
                h.set("X-User-Id", jwt.getSubject());
                h.set("X-User-Role", jwt.getClaimAsString("role"));
            })
            .uri(removeTokenQueryParam(request))
            .build();

        log.debug("WebSocket/SockJS request detected; injected headers from query param.");

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isWebSocketOrSockJsRequest(ServerHttpRequest request) {
        String upgrade = request.getHeaders().getFirst(HttpHeaders.UPGRADE);
        if (upgrade != null && "websocket".equalsIgnoreCase(upgrade)) {
            return true;
        }
        String path = request.getPath().value();
        return path != null && path.startsWith("/ws-auction");
    }

    private URI removeTokenQueryParam(ServerHttpRequest request) {
        MultiValueMap<String, String> params =
            new LinkedMultiValueMap<>(request.getQueryParams());
        params.remove(TOKEN_PARAM);
        return UriComponentsBuilder.fromUri(request.getURI())
            .replaceQueryParams(params)
            .build(true)
            .toUri();
    }

    @Override
    public int getOrder() {
        return 3;
    }
}

package com.smore.gateway.filter;

import com.smore.gateway.usecase.JwtValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtValidationFilter implements GlobalFilter, Ordered {

    private final JwtValidation jwtValidation;

    /*
    WhiteListFilter 에서 jwt 가 없는 요청은 이미 한번 거르기때문에
    해당 필터에서는 jwt 가 없으면 그대로 통과
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        var request = exchange.getRequest();

        String bearerJwt
            = request.getHeaders().getFirst("Authorization");
        log.info("jwt 필터 진입");
        if (bearerJwt == null) {
            log.info("jwt 없을때 헤더 진입");
            ServerHttpRequest newRequest = exchange.getRequest()
                .mutate()
                .headers(h -> {
                    h.add("X-User-Role", "NONE");
                })
                .build();
            return chain.filter(exchange.mutate()
                .request(newRequest)
                .build());
        }
        if (!bearerJwt.startsWith("Bearer ")) {
            log.error("Bearer Token is invalid (JwtValidationFilter)");
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        Jwt jwt;
        // Jwt 자체를 검증
        try {
            jwt = jwtValidation.validateJwt(bearerJwt.replace("Bearer ", ""));
        } catch (JwtException e) {
            log.error("fliter Jwt 검증단 오류 {}", e.getMessage());
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 내용 검증
        return jwtValidation.validateClaim(bearerJwt.replace("Bearer ", ""))
            .flatMap(isOk -> {
                if (!isOk) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                ServerHttpRequest newRequest = exchange.getRequest()
                    .mutate()
                    .headers(h -> {
                        h.add("X-User-Id", jwt.getSubject());
                        h.add("X-User-Role", jwt.getClaimAsString("role"));
                    })
                    .build();

                return chain.filter(
                    exchange.mutate()
                        .request(newRequest)
                        .build()
                );
            });
    }

    @Override
    public int getOrder() {
        return 2;
    }
}

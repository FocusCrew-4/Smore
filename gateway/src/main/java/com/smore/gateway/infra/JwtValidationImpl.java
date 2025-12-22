package com.smore.gateway.infra;

import com.smore.gateway.usecase.JwtValidation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationImpl implements JwtValidation {

    private final WebClient.Builder plainWebClient;

    private final WebClient.Builder loadBalancedWebClient;
    private final JwtDecoder jwtDecoder;

    @Value("${member.service.base-url}")
    private String memberBaseUrl;

    // TODO: 퀄리파이어 관련 공부
    public JwtValidationImpl(
        @Qualifier("plainWebClientBuilder") WebClient.Builder plainWebClient,
        @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder loadBalancedWebClient,
        JwtDecoder jwtDecoder
    ) {
        this.plainWebClient = plainWebClient;
        this.loadBalancedWebClient = loadBalancedWebClient;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Jwt validateJwt(String bearerJwt) {
        return jwtDecoder.decode(bearerJwt);
    }

    @Override
    public Mono<Boolean> validateClaim(String token) {

        Mono<Boolean> memberResponse = selectWebClientBuilder().build()
            // HTTP 메서드 종류 지정
            .method(HttpMethod.GET)
            // 요청을 보낼 uri 설정
            .uri(memberBaseUrl + "/api/v1/internal/members")
            // header 정보 설정
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            // retrieve 는 HTTP 요청을 보내고 서버의 응답을 비동기적으로 받기 위한 준비를 한다
            .retrieve()
            .bodyToMono(Boolean.class);

        return memberResponse;
    }
    private WebClient.Builder selectWebClientBuilder() {
        if (memberBaseUrl != null && memberBaseUrl.startsWith("lb://")) {
            return loadBalancedWebClient;
        }
        return plainWebClient;
    }
}

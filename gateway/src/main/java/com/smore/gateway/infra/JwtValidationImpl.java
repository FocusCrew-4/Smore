package com.smore.gateway.infra;

import com.smore.gateway.usecase.JwtValidation;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtValidationImpl implements JwtValidation {

    @Qualifier("plainWebClientBuilder")
    private final WebClient.Builder plainWebClient;

    @Qualifier("loadBalancedWebClientBuilder")
    private final WebClient.Builder loadBalancedWebClient;
    private final JwtDecoder jwtDecoder;

    @Value("${member.service.base-url}")
    private String memberBaseUrl;

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

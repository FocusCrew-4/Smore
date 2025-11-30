package com.smore.gateway.infra;

import com.smore.gateway.usecase.JwtValidation;
import lombok.RequiredArgsConstructor;
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

    private final WebClient.Builder webClient;
    private final JwtDecoder jwtDecoder;

    @Override
    public Jwt validateJwt(String bearerJwt) {
        return jwtDecoder.decode(bearerJwt);
    }

    @Override
    public Mono<Boolean> validateClaim(String token) {

//        Mono<Boolean> memberResponse = webClient.build()
//            // HTTP 메서드 종류 지정
//            .method(HttpMethod.GET)
//            // 요청을 보낼 uri 설정
//            .uri("")
//            // header 정보 설정
//            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//            // retrieve 는 HTTP 요청을 보내고 서버의 응답을 비동기적으로 받기 위한 준비를 한다
//            .retrieve()
//            .bodyToMono(Boolean.class);

        return Mono.just(true);
    }
}

package com.smore.gateway.usecase;

import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public interface JwtValidation {

    Jwt validateJwt(String bearerJwt);

    Mono<Boolean> validateClaim(String token);
}

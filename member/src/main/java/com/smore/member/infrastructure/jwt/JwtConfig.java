package com.smore.member.infrastructure.jwt;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.smore.member.domain.repository.MemberRepository;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class JwtConfig {
    @Value("dfkldfdfdfdfdfdfdfdfdfdfdfdfdfdfd")
    private String secret;

    @Bean
    JwtEncoder jwtEncoder() throws  Exception {
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    JwtDecoder jwtDecoder() throws  Exception {
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() throws  Exception {
        var converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("role");
        converter.setAuthorityPrefix("ROLE_");

        return converter;
    }

    @Bean
    TokenProvider tokenProvider() throws  Exception {
        return new TokenProvider(jwtEncoder());
    }

    // TODO: 공부 및 정리
    @Bean
    CustomJwtAuthenticationConverter customJwtAuthenticationConverter(
        MemberRepository memberRepository,
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter
    ) {
        return new CustomJwtAuthenticationConverter(memberRepository, jwtGrantedAuthoritiesConverter);
    }

}

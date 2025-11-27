package com.smore.member.infrastructure.jwt;

import com.smore.member.infrastructure.security.userdetails.CustomUserDetails;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@RequiredArgsConstructor
public class TokenProvider implements JwtProvider {

    private final JwtEncoder jwtEncoder;

    @Override
    public String generateAccessToken(CustomUserDetails user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(user.getUsername())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(1800))
            .claim("role", user.getRole())
            .claim("userId", user.getUserId().toString())
            .build();

        JwsHeader header = JwsHeader.with(() -> "HS256").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
            .getTokenValue();
    }
}

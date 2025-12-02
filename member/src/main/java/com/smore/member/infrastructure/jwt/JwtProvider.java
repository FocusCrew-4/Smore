package com.smore.member.infrastructure.jwt;

import com.smore.member.infrastructure.security.userdetails.CustomUserDetails;

public interface JwtProvider {
    String generateAccessToken(CustomUserDetails user);
}

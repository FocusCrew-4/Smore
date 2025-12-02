package com.smore.member.application.service.impl;

import com.smore.member.application.service.AuthService;
import com.smore.member.application.service.command.LoginCommand;
import com.smore.member.infrastructure.jwt.JwtProvider;
import com.smore.member.infrastructure.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public String login(LoginCommand command) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    command.email(),
                    command.password()
                )
            );
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return jwtProvider.generateAccessToken(customUserDetails);
        } catch (AuthenticationException e) {
            log.warn("Login Failed {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}


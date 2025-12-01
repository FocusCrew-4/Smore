package com.smore.member.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.smore.member.application.service.command.LoginCommand;
import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.vo.Credential;
import com.smore.member.infrastructure.jwt.JwtProvider;
import com.smore.member.infrastructure.security.userdetails.CustomUserDetails;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    Authentication authentication;

    @InjectMocks
    AuthServiceImpl authService;

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("성공 시 액세스 토큰을 반환한다")
        void returnsAccessTokenOnSuccess() {
            // given
            LoginCommand command = new LoginCommand("user@example.com", "password");
            CustomUserDetails userDetails = new CustomUserDetails(activeMember(command.email()));

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtProvider.generateAccessToken(userDetails)).thenReturn("token-value");

            // when
            String token = authService.login(command);

            // then
            assertThat(token).isEqualTo("token-value");
            verify(authenticationManager).authenticate(any());
            verify(jwtProvider).generateAccessToken(userDetails);
        }

        @Test
        @DisplayName("인증 실패 시 예외를 래핑해 던진다")
        void wrapsAuthenticationExceptionOnFailure() {
            // given
            LoginCommand command = new LoginCommand("user@example.com", "wrong-password");
            when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

            // when/then
            assertThatThrownBy(() -> authService.login(command))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(BadCredentialsException.class);

            verifyNoInteractions(jwtProvider);
        }

        private Member activeMember(String email) {
            return new Member(
                1L,
                Role.CONSUMER,
                new Credential(email, "encoded-pass"),
                "nickname",
                0,
                MemberStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
            );
        }
    }
}



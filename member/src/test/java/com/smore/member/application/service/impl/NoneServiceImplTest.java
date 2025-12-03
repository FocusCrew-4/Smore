package com.smore.member.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.smore.member.application.service.impl.member.NoneServiceImpl;
import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class NoneServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAppMapper mapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Clock clock;

    @InjectMocks
    NoneServiceImpl service;

    @Test
    void createMember_success() {
        // given
        CreateCommand command = new CreateCommand(
            Role.CONSUMER,
            "user@example.com",
            "plain-pass",
            "nickname"
        );

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);

        Member saved = Member.create(
            Role.CONSUMER,
            command.getEmail(),
            "encoded-pass",
            command.getNickname(),
            fixedClock
        );

        MemberResult expectedResult = new MemberResult(
            1L,
            Role.CONSUMER,
            command.getEmail(),
            command.getNickname(),
            0,
            saved.getStatus(),
            null,
            null,
            null,
            null
        );

        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
        when(passwordEncoder.encode(command.getPassword())).thenReturn("encoded-pass");
        when(memberRepository.save(any(Member.class))).thenReturn(saved);
        when(mapper.toMemberResult(saved)).thenReturn(expectedResult);

        // when
        MemberResult result = service.createMember(command);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }
}

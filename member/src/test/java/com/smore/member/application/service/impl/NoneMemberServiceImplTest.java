package com.smore.member.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class NoneMemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAppMapper mapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    NoneMemberServiceImpl service;

    @Test
    void getSupportedRole() {
        assertThat(service.getSupportedRole()).isEqualTo(Role.NONE);
    }

    @Test
    void createMember_success() {
        // given
        CreateCommand command = new CreateCommand(
            Role.CONSUMER,
            "user@example.com",
            "plain-pass",
            "nickname"
        );

        Member saved = Member.create(
            Role.CONSUMER,
            command.getEmail(),
            "encoded-pass",
            command.getNickname()
        );

        MemberResult expectedResult = new MemberResult(
            1L,
            Role.CONSUMER,
            command.getEmail(),
            command.getNickname(),
            0,
            saved.getStatus(),
            null
        );

        when(passwordEncoder.encode(command.getPassword())).thenReturn("encoded-pass");
        when(memberRepository.save(any(Member.class))).thenReturn(saved);
        when(mapper.toMemberResult(saved)).thenReturn(expectedResult);

        // when
        MemberResult result = service.createMember(command);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }
}

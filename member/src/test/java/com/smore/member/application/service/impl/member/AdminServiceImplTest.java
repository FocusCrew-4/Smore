package com.smore.member.application.service.impl.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import com.smore.member.application.service.command.FindCommand;
import com.smore.member.application.service.command.InfoUpdateCommand;
import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import com.smore.member.domain.vo.Credential;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAppMapper memberAppMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AdminServiceImpl adminService;

    @Test
    @DisplayName("관리자는 대상 회원을 ID로 조회한다")
    void adminReadsMemberById() {
        // given
        Long targetId = 10L;
        FindCommand command = new FindCommand(99L, targetId);
        Member member = member(targetId, Role.CONSUMER);
        MemberResult expected = new MemberResult(
            member.getId(),
            member.getRole(),
            member.getCredential().email(),
            member.getNickname(),
            member.getAuctionCancelCount(),
            member.getStatus(),
            member.getCreatedAt(),
            member.getUpdatedAt(),
            member.getDeletedAt(),
            member.getDeletedBy()
        );

        when(memberRepository.findById(targetId)).thenReturn(member);
        when(memberAppMapper.toMemberResult(member)).thenReturn(expected);

        // when
        MemberResult result = adminService.findMember(command);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("대상 회원이 없으면 예외를 던진다")
    void throwsWhenMemberNotFound() {
        // given
        Long targetId = 10L;
        FindCommand command = new FindCommand(99L, targetId);
        when(memberRepository.findById(targetId)).thenReturn(null);

        // when / then
        assertThatThrownBy(() -> adminService.findMember(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("회원이 존재하지 않습니다");
    }

    @Test
    @DisplayName("관리자는 닉네임/이메일/비밀번호를 모두 수정한다")
    void updateMemberAllFields() {
        // given
        Long targetId = 10L;
        Member member = member(targetId, Role.CONSUMER);

        InfoUpdateCommand command = new InfoUpdateCommand(
            1L,
            targetId,
            "new-nick",
            "new@example.com",
            "plain-pass"
        );

        MemberResult expected = new MemberResult(
            member.getId(),
            member.getRole(),
            command.email(),
            command.nickname(),
            member.getAuctionCancelCount(),
            member.getStatus(),
            member.getCreatedAt(),
            member.getUpdatedAt(),
            member.getDeletedAt(),
            member.getDeletedBy()
        );

        when(memberRepository.findById(targetId)).thenReturn(member);
        when(passwordEncoder.encode(command.password())).thenReturn("encoded-pass");
        when(memberRepository.save(member)).thenReturn(member);
        when(memberAppMapper.toMemberResult(member)).thenReturn(expected);

        // when
        MemberResult result = adminService.update(command);

        // then
        assertThat(result).isEqualTo(expected);
        assertThat(member.getCredential().email()).isEqualTo(command.email());
        assertThat(member.getCredential().password()).isEqualTo("encoded-pass");
        assertThat(member.getNickname()).isEqualTo(command.nickname());
        verify(passwordEncoder).encode(command.password());
    }

    @Test
    @DisplayName("관리자는 null 필드는 그대로 두고, 전달된 필드만 수정한다")
    void updateMemberPartial() {
        // given
        Long targetId = 11L;
        Member member = member(targetId, Role.SELLER);

        String originalEmail = member.getCredential().email();
        String originalPassword = member.getCredential().password();
        String originalNickname = member.getNickname();

        InfoUpdateCommand command = new InfoUpdateCommand(
            1L,
            targetId,
            null,
            null,
            null
        );

        MemberResult expected = new MemberResult(
            member.getId(),
            member.getRole(),
            originalEmail,
            originalNickname,
            member.getAuctionCancelCount(),
            member.getStatus(),
            member.getCreatedAt(),
            member.getUpdatedAt(),
            member.getDeletedAt(),
            member.getDeletedBy()
        );

        when(memberRepository.findById(targetId)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(member);
        when(memberAppMapper.toMemberResult(member)).thenReturn(expected);

        // when
        MemberResult result = adminService.update(command);

        // then
        assertThat(result).isEqualTo(expected);
        assertThat(member.getCredential().email()).isEqualTo(originalEmail);
        assertThat(member.getCredential().password()).isEqualTo(originalPassword);
        assertThat(member.getNickname()).isEqualTo(originalNickname);
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
    }

    private Member member(Long id, Role role) {
        return new Member(
            id,
            role,
            new Credential("target@example.com", "pw"),
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

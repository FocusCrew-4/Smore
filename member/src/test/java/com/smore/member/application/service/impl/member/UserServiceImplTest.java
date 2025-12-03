package com.smore.member.application.service.impl.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class UserServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAppMapper memberAppMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("사용자는 자신의 정보를 조회한다")
    void findOwnInfo() {
        // given
        Long userId = 1L;
        FindCommand command = new FindCommand(userId, userId);
        Member member = member(userId, Role.USER);

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

        when(memberRepository.findById(userId)).thenReturn(member);
        when(memberAppMapper.toMemberResult(member)).thenReturn(expected);

        // when
        MemberResult result = userService.findMember(command);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("사용자는 자신의 닉네임/이메일/비밀번호를 수정할 수 있다")
    void updateOwnInfo() {
        // given
        Long userId = 2L;
        Member member = member(userId, Role.USER);

        InfoUpdateCommand command = new InfoUpdateCommand(
            userId,
            userId,
            "changed",
            "changed@example.com",
            "new-pass"
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

        when(memberRepository.findById(userId)).thenReturn(member);
        when(passwordEncoder.encode(command.password())).thenReturn("encoded-pass");
        when(memberRepository.save(member)).thenReturn(member);
        when(memberAppMapper.toMemberResult(member)).thenReturn(expected);

        // when
        MemberResult result = userService.update(command);

        // then
        assertThat(result).isEqualTo(expected);
        assertThat(member.getNickname()).isEqualTo(command.nickname());
        assertThat(member.getCredential().email()).isEqualTo(command.email());
        assertThat(member.getCredential().password()).isEqualTo("encoded-pass");
        verify(passwordEncoder).encode(command.password());
    }

    @Test
    @DisplayName("사용자는 null로 전달된 필드는 수정하지 않는다")
    void updateSkipsNullFields() {
        // given
        Long userId = 3L;
        Member member = member(userId, Role.USER);

        String originalEmail = member.getCredential().email();
        String originalPassword = member.getCredential().password();
        String originalNickname = member.getNickname();

        InfoUpdateCommand command = new InfoUpdateCommand(
            userId,
            userId,
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

        when(memberRepository.findById(userId)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(member);
        when(memberAppMapper.toMemberResult(member)).thenReturn(expected);

        // when
        MemberResult result = userService.update(command);

        // then
        assertThat(result).isEqualTo(expected);
        assertThat(member.getCredential().email()).isEqualTo(originalEmail);
        assertThat(member.getCredential().password()).isEqualTo(originalPassword);
        assertThat(member.getNickname()).isEqualTo(originalNickname);
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("사용자는 타인의 정보를 조회/수정하려 하면 예외를 던진다")
    void throwsWhenAccessingOthersInfo() {
        // given
        Long requesterId = 1L;
        Long targetId = 99L;

        FindCommand findCommand = new FindCommand(requesterId, targetId);
        InfoUpdateCommand updateCommand = new InfoUpdateCommand(
            requesterId,
            targetId,
            null,
            null,
            null
        );

        // when / then
        assertThatThrownBy(() -> userService.findMember(findCommand))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("자신의 정보만 조회 가능합니다");

        assertThatThrownBy(() -> userService.update(updateCommand))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("자신의 정보만 조회 가능합니다");
    }

    private Member member(Long id, Role role) {
        return new Member(
            id,
            role,
            new Credential("user" + id + "@example.com", "pw-" + id),
            "user-" + id,
            0,
            MemberStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            null
        );
    }
}

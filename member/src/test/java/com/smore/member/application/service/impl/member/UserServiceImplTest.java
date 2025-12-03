package com.smore.member.application.service.impl.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.smore.member.application.service.command.FindCommand;
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

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAppMapper memberAppMapper;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("요청자와 대상이 동일하면 사용자 정보를 반환한다")
    void returnsMemberWhenRequesterIsTarget() {
        // given
        Long requesterId = 1L;
        FindCommand command = new FindCommand(requesterId, requesterId);
        Member member = member(requesterId, Role.USER);
        MemberResult expected = new MemberResult(
            member.getId(),
            member.getRole(),
            member.getCredential().email(),
            member.getNickname(),
            member.getAuctionCancelCount(),
            member.getStatus(),
            member.getDeletedBy()
        );

        when(memberRepository.findById(requesterId)).thenReturn(member);
        when(memberAppMapper.toMemberResult(member)).thenReturn(expected);

        // when
        MemberResult result = userService.findMember(command);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("요청자와 대상이 다르면 예외를 던진다")
    void throwsWhenRequesterDiffersFromTarget() {
        // given
        Long requesterId = 1L;
        Long targetId = 2L;
        FindCommand command = new FindCommand(requesterId, targetId);
        Member member = member(targetId, Role.USER);

        when(memberRepository.findById(targetId)).thenReturn(member);

        // when / then
        assertThatThrownBy(() -> userService.findMember(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("자신의 정보만 조회 가능합니다");
    }

    @Test
    @DisplayName("대상 회원이 없으면 예외를 던진다")
    void throwsWhenMemberNotFound() {
        // given
        FindCommand command = new FindCommand(1L, 2L);
        when(memberRepository.findById(2L)).thenReturn(null);

        // when / then
        assertThatThrownBy(() -> userService.findMember(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("회원이 존재하지 않습니다");
    }

    private Member member(Long id, Role role) {
        return new Member(
            id,
            role,
            new Credential("user@example.com", "pw"),
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

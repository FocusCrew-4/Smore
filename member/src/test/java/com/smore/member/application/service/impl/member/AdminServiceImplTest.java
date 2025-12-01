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
class AdminServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberAppMapper memberAppMapper;

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

package com.smore.member.application.service.impl.member;

import com.smore.member.application.service.command.DeleteCommand;
import com.smore.member.application.service.command.FindCommand;
import com.smore.member.application.service.command.InfoUpdateCommand;
import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.usecase.MemberDelete;
import com.smore.member.application.service.usecase.MemberFind;
import com.smore.member.application.service.usecase.MemberInfoUpdate;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl
    implements MemberFind, MemberInfoUpdate, MemberDelete {

    private final MemberRepository repository;
    private final MemberAppMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Role getSupportedRole() {
        return Role.USER;
    }


    @Override
    public MemberResult findMember(FindCommand findCommand) {
        verifyRequesterIsOwner(findCommand.requesterId(), findCommand.targetId());
        Member member = repository.findById(findCommand.targetId());
        return mapper.toMemberResult(member);
    }

    @Override
    @Transactional
    public MemberResult update(InfoUpdateCommand command) {
        verifyRequesterIsOwner(command.requesterId(), command.targetId());
        Member member = repository.findById(command.targetId());
        if (command.nickname() != null) {
            member.updateNickname(command.nickname());
        }
        if (command.email() != null) {
            member.changeEmail(command.email());
        }
        if (command.password() != null) {
            member.changePassword(
                passwordEncoder.encode(command.password())
            );
        }
        Member updatedMember = repository.save(member);
        return mapper.toMemberResult(updatedMember);
    }

    // TODO: 에러 타입 변경 필요
    private void verifyRequesterIsOwner(Long requesterId, Long targetId) {
        if (!requesterId.equals(targetId)) {
            throw new RuntimeException("자신의 정보만 조회 가능합니다");
        }
    }

    @Override
    @Transactional
    public MemberResult delete(DeleteCommand deleteCommand) {
        verifyRequesterIsOwner(deleteCommand.requesterId(), deleteCommand.targetId());
        Member findMember = repository.findById(deleteCommand.targetId());
        findMember.deleteMember(deleteCommand.requesterId());
        Member deletedMember = repository.save(findMember);
        return mapper.toMemberResult(deletedMember);
    }
}

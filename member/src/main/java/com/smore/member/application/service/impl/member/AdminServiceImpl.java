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
public class AdminServiceImpl
    implements MemberFind, MemberInfoUpdate, MemberDelete {

    private final MemberRepository repository;
    private final MemberAppMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Role getSupportedRole() {
        return Role.ADMIN;
    }

    @Override
    public MemberResult findMember(FindCommand findCommand) {
        Member member = repository.findById(findCommand.targetId());
        if (member == null) {
            throw new RuntimeException("회원이 존재하지 않습니다");
        }
        return mapper.toMemberResult(member);
    }

    @Override
    @Transactional
    public MemberResult update(InfoUpdateCommand command) {
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

    @Override
    @Transactional
    public MemberResult delete(DeleteCommand deleteCommand) {
        Member findMember = repository.findById(deleteCommand.targetId());
        findMember.deleteMember(deleteCommand.requesterId());
        Member deletedMember = repository.save(findMember);
        return mapper.toMemberResult(deletedMember);
    }
}

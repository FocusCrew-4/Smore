package com.smore.member.application.service.impl.member;

import com.smore.member.application.service.command.FindCommand;
import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.usecase.MemberFind;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl
    implements RoleSupportable, MemberFind {

    private final MemberRepository repository;
    private final MemberAppMapper mapper;

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
}

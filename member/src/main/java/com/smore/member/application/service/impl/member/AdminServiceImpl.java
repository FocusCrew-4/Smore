package com.smore.member.application.service.impl.member;

import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.usecase.MemberRead;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl
    implements RoleSupportable, MemberRead {

    private final MemberRepository repository;
    private final MemberAppMapper mapper;

    @Override
    public Role getSupportedRole() {
        return Role.ADMIN;
    }

    @Override
    public MemberResult readMember() {
        return null;
    }
}

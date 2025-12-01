package com.smore.member.application.service.impl;

import com.smore.member.application.service.RoleBasedMemberService;
import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.domain.repository.MemberRepository;

public abstract class AbstractMemberService implements RoleBasedMemberService {

    protected final MemberRepository memberRepository;
    protected final MemberAppMapper mapper;

    protected AbstractMemberService(MemberRepository memberRepository
    , MemberAppMapper mapper) {
        this.memberRepository = memberRepository;
        this.mapper = mapper;
    }

}

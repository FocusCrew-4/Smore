package com.smore.member.application.service.impl;

import com.smore.member.application.service.usecase.MemberRoleChange;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberRoleChangeImpl
    implements MemberRoleChange {

    private final MemberRepository repository;

    @Override
    public void toSeller(Long targetId) {
        Member member = repository.findById(targetId);
        member.toSeller();
        repository.save(member);
    }
}

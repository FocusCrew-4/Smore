package com.smore.member.domain.repository;

import com.smore.member.domain.model.Member;

public interface MemberRepository {
    Member findByEmail(String email);

    Member save(Member member);

    Member findById(Long id);
}

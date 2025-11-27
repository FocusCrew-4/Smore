package com.smore.member.infrastructure.persistance;

import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import com.smore.member.infrastructure.persistance.jpa.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member findByEmail() {
        return null;
    }
}

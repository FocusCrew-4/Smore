package com.smore.member.infrastructure.persistance;

import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import com.smore.member.infrastructure.persistance.jpa.entity.MemberJpa;
import com.smore.member.infrastructure.persistance.jpa.mapper.MemberJpaMapper;
import com.smore.member.infrastructure.persistance.jpa.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaMapper mapper;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member findByEmail(String email) {
        MemberJpa memberJpa = memberJpaRepository.findByCredentialEmail(email);

        return memberJpa != null ? mapper.toDomain(memberJpa) : null;
    }

    @Override
    public Member save(Member member) {
        MemberJpa memberJpa = memberJpaRepository.save(mapper.toEntity(member));

        return mapper.toDomain(memberJpa);
    }
}

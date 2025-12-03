package com.smore.member.infrastructure.persistence;

import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import com.smore.member.infrastructure.persistence.jpa.entity.MemberJpa;
import com.smore.member.infrastructure.persistence.jpa.mapper.MemberJpaMapper;
import com.smore.member.infrastructure.persistence.jpa.repository.MemberJpaRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaMapper mapper;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member findByEmail(String email) {
        MemberJpa memberJpa = memberJpaRepository.findByCredentialEmail(email)
            .orElseThrow(() -> new NoSuchElementException("member not found"));

        return mapper.toDomain(memberJpa);
    }

    @Override
    public Member save(Member member) {
        MemberJpa memberJpa = memberJpaRepository.save(mapper.toEntity(member));

        return mapper.toDomain(memberJpa);
    }

    @Override
    public Member findById(Long id) {
        MemberJpa memberJpa = memberJpaRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Member not found: " + id));
        return mapper.toDomain(memberJpa);
    }
}

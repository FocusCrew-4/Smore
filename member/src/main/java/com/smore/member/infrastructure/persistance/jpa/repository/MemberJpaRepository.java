package com.smore.member.infrastructure.persistance.jpa.repository;

import com.smore.member.infrastructure.persistance.jpa.entity.MemberJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberJpa, Long> {

    MemberJpa findByCredentialEmail(String email);
}

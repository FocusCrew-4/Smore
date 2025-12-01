package com.smore.member.infrastructure.persistance.jpa.repository;

import com.smore.member.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

}

package com.smore.seller.infrastructure.persistence.jpa.repository;

import com.smore.seller.infrastructure.persistence.jpa.entity.SellerJpa;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerJpaRepository extends JpaRepository<SellerJpa, UUID> {

    Optional<SellerJpa> findByMemberId(Long memberId);
}

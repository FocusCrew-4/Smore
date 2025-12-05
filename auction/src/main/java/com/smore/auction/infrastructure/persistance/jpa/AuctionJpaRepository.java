package com.smore.auction.infrastructure.persistance.jpa;

import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionJpa;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionJpaRepository extends JpaRepository<AuctionJpa, UUID> {

    Optional<AuctionJpa> findByProductId(UUID productId);
}

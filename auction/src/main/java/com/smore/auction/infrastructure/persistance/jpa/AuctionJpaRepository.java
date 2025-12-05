package com.smore.auction.infrastructure.persistance.jpa;

import com.smore.auction.domain.model.Auction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionJpaRepository extends JpaRepository<Auction, UUID> {

}

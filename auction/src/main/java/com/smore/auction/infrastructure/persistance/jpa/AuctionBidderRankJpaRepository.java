package com.smore.auction.infrastructure.persistance.jpa;

import com.smore.auction.domain.model.AuctionBidderRank;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionBidderRankJpaRepository extends JpaRepository<AuctionBidderRank, UUID> {

}

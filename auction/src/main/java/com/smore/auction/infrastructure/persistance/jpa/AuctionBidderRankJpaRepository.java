package com.smore.auction.infrastructure.persistance.jpa;

import com.smore.auction.domain.model.AuctionBidderRank;
import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionBidderRankJpa;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionBidderRankJpaRepository extends JpaRepository<AuctionBidderRankJpa, UUID> {

}

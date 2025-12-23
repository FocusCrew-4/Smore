package com.smore.auction.infrastructure.persistance.jpa;

import com.smore.auction.domain.enums.BidderStatus;
import com.smore.auction.domain.vo.Bidder;
import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionBidderRankJpa;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionBidderRankJpaRepository extends JpaRepository<AuctionBidderRankJpa, UUID> {

    Optional<AuctionBidderRankJpa> findByAuctionIdAndBidder_Id(UUID uuid, Long userId);
    Optional<AuctionBidderRankJpa> findTop1ByStatusAndAuctionIdOrderByRankAsc(BidderStatus pending, UUID auctionId);
    Optional<AuctionBidderRankJpa> findByStatusAndAuctionIdAndBidder_Id(BidderStatus winner, UUID auctionId, Long winnerId);
}

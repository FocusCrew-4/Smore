package com.smore.auction.application.sql;

import com.smore.auction.domain.model.Auction;
import com.smore.auction.domain.model.AuctionBidderRank;
import java.util.List;
import java.util.UUID;

public interface AuctionSqlRepository {
    Auction save(Auction auction);

    Auction findByProductId(UUID uuid);

    Auction findById(String auctionId);

    void saveAll(List<AuctionBidderRank> ranks);

    AuctionBidderRank findBidByAuctionIdAndBidderId(String auctionId, Long userId);

    // TODO: 추후 DDD 에 맞는지 재검토 필요 - 이유: Stock 값에 영향받아서 개인이 바뀌면 곤란함
    void saveBidder(AuctionBidderRank auctionBidderRank);
}

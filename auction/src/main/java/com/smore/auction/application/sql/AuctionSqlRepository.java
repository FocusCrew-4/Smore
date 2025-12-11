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
}

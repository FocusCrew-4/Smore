package com.smore.auction.application.sql;

import com.smore.auction.domain.model.Auction;
import java.util.UUID;

public interface AuctionSqlRepository {
    Auction save(Auction auction);

    Auction findByProductId(UUID uuid);
}

package com.smore.auction.application.port.out;

import com.smore.auction.domain.model.Auction;
import java.time.Duration;

public interface AuctionRoomRegistry {
    void register(Auction auction, Duration ttl);
}

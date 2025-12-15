package com.smore.auction.application.port.out;

import java.time.Duration;
import java.util.UUID;

public interface AuctionRoomRegistry {
    void register(UUID auctionId, Duration ttl);
}

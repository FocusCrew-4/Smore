package com.smore.auction.application.port.out;

import com.smore.auction.infrastructure.outbox.AuctionOutbox;

public interface AuctionOutboxPort {
    void saveEvent(AuctionOutbox event);
}

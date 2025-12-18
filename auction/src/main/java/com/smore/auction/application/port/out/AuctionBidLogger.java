package com.smore.auction.application.port.out;

import java.util.UUID;

public interface AuctionBidLogger {

    void writeBidLog(UUID auctionId, Long bidderId, Integer quantity, Double bidPrice);

}

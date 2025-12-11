package com.smore.auction.application.service.usecase;

import com.smore.auction.application.result.AuctionBidCalculateResult;
import java.math.BigDecimal;

public interface AuctionBidCalculator {
    AuctionBidCalculateResult calculateBid(BigDecimal bidPrice, Integer quantity, String auctionId, String userId);
}

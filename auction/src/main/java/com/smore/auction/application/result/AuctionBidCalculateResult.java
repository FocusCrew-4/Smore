package com.smore.auction.application.result;

import java.math.BigDecimal;

public record AuctionBidCalculateResult(
    BigDecimal highestBid,
    BigDecimal minQualifyingBid,
    BigDecimal myBid,
    BigDecimal myBidRank
) {

}

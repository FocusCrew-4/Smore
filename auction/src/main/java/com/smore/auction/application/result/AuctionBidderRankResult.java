package com.smore.auction.application.result;

import com.smore.auction.domain.enums.BidderStatus;
import java.math.BigDecimal;

public record AuctionBidderRankResult(
    BigDecimal price,
    Integer quantity,
    BidderStatus status,
    Long rank
) {

}

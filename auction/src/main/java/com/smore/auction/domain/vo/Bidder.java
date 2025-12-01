package com.smore.auction.domain.vo;

import com.smore.auction.domain.enums.BidderStatus;

public record Bidder(
    Long id,
    Long price,
    Integer quantity,
    BidderStatus status
) {
}

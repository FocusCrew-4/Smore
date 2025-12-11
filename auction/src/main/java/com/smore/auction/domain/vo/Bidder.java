package com.smore.auction.domain.vo;

import com.smore.auction.domain.enums.BidderStatus;
import java.math.BigDecimal;

public record Bidder(
    Long id,
    BigDecimal price,
    Integer quantity
) {
}

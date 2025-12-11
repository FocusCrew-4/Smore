package com.smore.auction.presentation.websocket.dto.response;

import java.math.BigDecimal;

public record AuctionBidResponseDto(
    BigDecimal highestBid,
    BigDecimal minQualifyingBid
) {

}

package com.smore.auction.presentation.websocket.dto.request;

import java.math.BigDecimal;

public record AuctionBidRequestDto(
    BigDecimal bidPrice,
    Integer quantity
) {

}

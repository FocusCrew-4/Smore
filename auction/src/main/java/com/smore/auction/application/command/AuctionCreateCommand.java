package com.smore.auction.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionCreateCommand(
    UUID productId,
    BigDecimal productPrice,
    Long stock,
    Long sellerId
) {

}

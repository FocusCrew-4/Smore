package com.smore.auction.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionCreateCommand(
    UUID productId,
    UUID productCategoryId,
    BigDecimal productPrice,
    Long stock,
    Long sellerId
) {

}

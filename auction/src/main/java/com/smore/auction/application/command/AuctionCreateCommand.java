package com.smore.auction.application.command;

import java.util.UUID;

public record AuctionCreateCommand(
    UUID productId,
    Long productPrice,
    Long stock,
    Long sellerId
) {

}

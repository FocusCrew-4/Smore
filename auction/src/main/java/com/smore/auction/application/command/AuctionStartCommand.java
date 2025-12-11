package com.smore.auction.application.command;

import java.time.Duration;
import java.util.UUID;

public record AuctionStartCommand(
    UUID productId,
    Duration duration,
    UUID idempotencyKey
) {

}

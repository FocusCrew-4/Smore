package com.smore.auction.infrastructure.kafka.listener.dto;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AuctionStartedV1(
    UUID productId,
    Duration expireAfter,
    UUID idempotencyKey,
    OffsetDateTime createdAt
) {

}

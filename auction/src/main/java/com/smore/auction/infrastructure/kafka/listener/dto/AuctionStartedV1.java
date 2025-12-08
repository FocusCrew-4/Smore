package com.smore.auction.infrastructure.kafka.listener.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AuctionStartedV1(
    Long sellerId,
    UUID productId,
    Long productPrice,
    Long stock,
    UUID idempotencyKey,
    OffsetDateTime createdAt
) {

}

package com.smore.auction.infrastructure.kafka.listener.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AuctionPendingStartedV1(
    Long sellerId,
    UUID productId,
    UUID productCategoryId,
    BigDecimal productPrice,
    Long stock,
    UUID idempotencyKey,
    OffsetDateTime createdAt
) {

}

package com.smore.auction.infrastructure.kafka.listener.dto;

import java.util.UUID;

public record OrderFailedV1(
    UUID productId,
    Long userId,
    UUID idempotencyKey
) {

}

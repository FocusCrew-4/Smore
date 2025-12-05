package com.smore.member.infrastructure.kafka.listener.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SellerRegisterV1(
    Long memberId,
    UUID idempotencyKey,
    OffsetDateTime createdAt
) {

}

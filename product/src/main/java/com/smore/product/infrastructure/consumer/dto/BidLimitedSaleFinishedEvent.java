package com.smore.product.infrastructure.consumer.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BidLimitedSaleFinishedEvent {
    private UUID productId;
    private Integer soldQuantity;
    private UUID bidId;
    private UUID idempotencyKey;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
}
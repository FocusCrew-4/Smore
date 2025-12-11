package com.smore.product.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuctionPendingStartEvent {

    private Long sellerId;
    private UUID productId;
    private UUID categoryId;
    private Long productPrice;
    private int stock;

    private UUID idempotencyKey;
    private LocalDateTime createdAt;
}
package com.smore.product.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LimitedSalePendingStartEvent {

    private UUID productId;
    private UUID categoryId;
    private Long sellerId;
    private BigDecimal productPrice;
    private int stock;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private UUID idempotencyKey;
}

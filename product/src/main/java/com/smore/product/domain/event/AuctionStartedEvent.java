package com.smore.product.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuctionStartedEvent {

    private UUID productId;
    private Duration biddingDuration;

    private UUID idempotencyKey;
    private LocalDateTime createdAt;
}
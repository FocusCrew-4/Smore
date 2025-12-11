package com.smore.product.domain.event;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BidLimitedSaleFinishedEvent {

    private UUID productId;
    private Integer soldQuantity;
    private UUID bidId;
    private String idempotencyKey;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    // JSON 역직렬화하려면 기본 생성자 필요
    public BidLimitedSaleFinishedEvent() {}
}
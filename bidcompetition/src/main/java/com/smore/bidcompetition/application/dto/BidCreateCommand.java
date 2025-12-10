package com.smore.bidcompetition.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidCreateCommand {

    private UUID productId;
    private UUID categoryId;
    private Long sellerId;
    private BigDecimal productPrice;
    private Integer stock;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private UUID idempotencyKey;

    public static BidCreateCommand of(
        UUID productId,
        UUID categoryId,
        Long sellerId,
        BigDecimal productPrice,
        Integer stock,
        LocalDateTime startAt,
        LocalDateTime endAt,
        UUID idempotencyKey
    ) {

        return BidCreateCommand.builder()
            .productId(productId)
            .categoryId(categoryId)
            .sellerId(sellerId)
            .productPrice(productPrice)
            .stock(stock)
            .startAt(startAt)
            .endAt(endAt)
            .idempotencyKey(idempotencyKey)
            .build();
    }
}

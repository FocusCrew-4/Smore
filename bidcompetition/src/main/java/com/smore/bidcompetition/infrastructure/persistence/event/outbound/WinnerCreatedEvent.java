package com.smore.bidcompetition.infrastructure.persistence.event.outbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WinnerCreatedEvent implements BidEvent {

    private Long userId;
    private UUID productId;
    private Integer productPrice;
    private Integer quantity;
    private UUID categoryId;
    private String saleType;
    private Long sellerId;
    private UUID idempotencyKey;
    private LocalDateTime expiresAt;
    private String street;
    private String city;
    private String zipcode;

    public static WinnerCreatedEvent of(
        Long userId,
        UUID productId,
        Integer productPrice,
        Integer quantity,
        UUID categoryId,
        Long sellerId,
        UUID idempotencyKey,
        LocalDateTime expiresAt,
        String street,
        String city,
        String zipcode
    ) {
        return WinnerCreatedEvent.builder()
            .userId(userId)
            .productId(productId)
            .productPrice(productPrice)
            .quantity(quantity)
            .categoryId(categoryId)
            .saleType("BID")
            .sellerId(sellerId)
            .idempotencyKey(idempotencyKey)
            .expiresAt(expiresAt)
            .street(street)
            .city(city)
            .zipcode(zipcode)
            .build();
    }
}

package com.smore.order.application.dto;

import com.smore.order.domain.status.SaleType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOrderCommand {

    private Long userId;
    private UUID productId;
    private Integer productPrice;
    private Integer quantity;
    private UUID categoryId;
    private SaleType saleType;
    private Long sellerId;
    private UUID idempotencyKey;
    private LocalDateTime expiresAt;
    private String street;
    private String city;
    private String zipcode;

    public static CreateOrderCommand create(
        Long userId, UUID productId,
        Integer productPrice, Integer quantity,
        UUID categoryId, SaleType saleType, Long sellerId,
        UUID idempotencyKey, LocalDateTime expiresAt,
        String street, String city, String zipcode
    ) {

        return CreateOrderCommand.builder()
            .userId(userId)
            .productId(productId)
            .productPrice(productPrice)
            .quantity(quantity)
            .categoryId(categoryId)
            .saleType(saleType)
            .sellerId(sellerId)
            .idempotencyKey(idempotencyKey)
            .expiresAt(expiresAt)
            .street(street)
            .city(city)
            .zipcode(zipcode)
            .build();

    }
}




package com.smore.auction.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionWinnerConfirmV1 {
    Long userId;
    UUID productId;
    BigDecimal productPrice;
    Integer quantity;
    UUID categoryId;
    String saleType;
    Long sellerId;
    UUID idempotencyKey;
    LocalDateTime expiresAt;
    String street;
    String city;
    String zipcode;

    public static AuctionWinnerConfirmV1 create(
        Long userId,
        UUID productId,
        BigDecimal productPrice,
        Integer quantity,
        UUID categoryId,
        Long sellerId,
        UUID idempotencyKey
    ) {
        return new AuctionWinnerConfirmV1(
            userId,
            productId,
            productPrice,
            quantity,
            categoryId,
            "AUCTION",
            sellerId,
            idempotencyKey,
            null,
            null,
            null,
            null
        );
    }
}

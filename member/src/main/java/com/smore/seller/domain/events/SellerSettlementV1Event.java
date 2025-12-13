package com.smore.seller.domain.events;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerSettlementV1Event {

    private final Long id;
    private final BigDecimal amount;
    private final String accountNum;

    UUID idempotencyKey;
    LocalDateTime createdAt;

    public static SellerSettlementV1Event create(
        Long requesterId,
        BigDecimal amount,
        String accountNum,
        UUID idempotencyKey,
        Clock clock
    ) {
        return new SellerSettlementV1Event(
            requesterId,
            amount,
            accountNum,
            idempotencyKey,
            LocalDateTime.now(clock)
        );
    }

}

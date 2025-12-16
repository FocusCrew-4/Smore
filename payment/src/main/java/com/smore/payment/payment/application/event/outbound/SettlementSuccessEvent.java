package com.smore.payment.payment.application.event.outbound;


import java.math.BigDecimal;
import java.util.UUID;

public record SettlementSuccessEvent(
        Long userId,
        BigDecimal amount,
        String accountNumber,
        UUID idempotencyKey
) {
    public static SettlementSuccessEvent of(
            Long userId,
            BigDecimal amount,
            String accountNumber,
            UUID idempotencyKey
    ) {
        return new SettlementSuccessEvent(
                userId,
                amount,
                accountNumber,
                idempotencyKey
        );
    }
}

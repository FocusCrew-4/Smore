package com.smore.payment.payment.application.event.inbound;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
public record PaymentSettlementRequestEvent(
        Long userId,
        BigDecimal amount,
        String accountNumber,
        UUID idempotencyKey,
        LocalDateTime createdAt
) {
    public static PaymentSettlementRequestEvent of(
            Long userId,
            BigDecimal amount,
            String accountNumber,
            UUID idempotencyKey,
            LocalDateTime createdAt
    ) {
        return new PaymentSettlementRequestEvent(
                userId,
                amount,
                accountNumber,
                idempotencyKey,
                createdAt
        );
    }

}

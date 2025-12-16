package com.smore.payment.payment.application.event.outbound;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementFailedEvent(
        Long userId,
        BigDecimal amount,
        String accountNumber,
        UUID idempotencyKey,
        String failedReason
) {
    public static SettlementFailedEvent of(
            Long userId,
            BigDecimal amount,
            String accountNumber,
            UUID idempotencyKey,
            String failedReason
    ) {
        return new SettlementFailedEvent(
                userId,
                amount,
                accountNumber,
                idempotencyKey,
                failedReason
        );
    }
}

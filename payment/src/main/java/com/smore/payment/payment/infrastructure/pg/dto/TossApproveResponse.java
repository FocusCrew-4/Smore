package com.smore.payment.payment.infrastructure.pg.dto;

import java.time.LocalDateTime;

public record TossApproveResponse(
        String orderId,
        String method,
        String status,
        String transactionKey,
        Card card,
        LocalDateTime approvedAt
        ) {
    public record Card(
            String company,
            String number,
            Integer installmentPlanMonths,
            boolean isInterestFree,
            String cardType,
            String ownerType,
            String acquirerCode
    ) {}
}

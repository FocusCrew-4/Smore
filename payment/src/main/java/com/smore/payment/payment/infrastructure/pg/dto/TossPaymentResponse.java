package com.smore.payment.payment.infrastructure.pg.dto;

import java.util.List;

public record TossPaymentResponse(
        String paymentKey,
        String type,
        String orderId,
        String orderName,
        String currency,
        String lastTransactionKey,
        Long totalAmount,
        Long balanceAmount,
        String status,
        String method,
        Card card,
        String requestedAt,
        String approvedAt,
        Failure failure,
        List<Cancels> cancels
) {
    public record Card(
            String issuerCode,
            String acquirerCode,
            String number,
            Integer installmentPlanMonths,
            boolean isInterestFree,
            String approveNo,
            String cardType,
            String ownerType,
            String acquireStatus,
            Long amount
    ) {}
    public record Failure(
            String code,
            String message
    ) {}
    public record Cancels(
            Long cancelAmount,
            String cancelReason,
            Long refundableAmount,
            String canceledAt,
            String transactionKey,
            String cancelStatus
    ) {}
}


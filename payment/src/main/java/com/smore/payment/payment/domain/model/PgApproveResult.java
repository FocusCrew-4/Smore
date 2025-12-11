package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PgApproveResult(
        // PG 정보
        String pgProvider,
        String paymentKey,
        String pgOrderId,
        String pgOrderName,
        String transactionKey,       // lastTransactionKey
        String pgStatus,
        String paymentMethod,        // method
        String currency,
        BigDecimal totalAmount,
        BigDecimal balanceAmount,

        // 카드 정보
        String cardIssuerCode,
        String cardAcquirerCode,
        String cardNumber,
        Integer installmentPlanMonths,
        boolean interestFree,
        String approveNo,
        String cardType,
        String cardOwnerType,
        String cardAcquireStatus,
        BigDecimal cardAmount,

        // 승인 / 요청 시각
        LocalDateTime requestedAt,
        LocalDateTime approvedAt,

        String failureCode,
        String failureMessage,

        CancellationInfo cancels

) {
    public record CancellationInfo(
            BigDecimal cancelAmount,
            String cancelReason,
            BigDecimal refundableAmount,
            LocalDateTime canceledAt,
            String cancelTransactionKey,
            String cancelStatus
    ) {}
}

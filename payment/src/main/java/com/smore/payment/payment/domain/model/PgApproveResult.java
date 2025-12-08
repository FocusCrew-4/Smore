package com.smore.payment.payment.domain.model;

import java.time.LocalDateTime;

public record PgApproveResult(
        // PG 정보
        String pgProvider,        // Toss, Kakao 등
        String pgOrderId,
        String pgTransactionKey,
        String pgStatus,
        String pgMessage,

        // 카드 정보
        String cardCompany,
        String cardNumber,
        Integer installmentMonths,
        boolean interestFree,
        String cardType,
        String ownerType,
        String acquirerCode,

        LocalDateTime approvedAt
) {
}

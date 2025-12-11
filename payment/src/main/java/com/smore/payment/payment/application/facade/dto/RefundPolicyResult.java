package com.smore.payment.payment.application.facade.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public record RefundPolicyResult(
        Duration refundPeriodDays,
        String refundFeeType,
        BigDecimal refundFeeRate,
        BigDecimal refundFixedAmount,
        boolean refundable
) {
    /**
     * 수수료 계산 메서드 — 필요 시 사용
     */
    public BigDecimal calculateRefundFee(BigDecimal originalAmount) {

        return switch (refundFeeType.toUpperCase()) {
            case "RATE" -> originalAmount.multiply(refundFeeRate); // 비율 수수료
            case "FIXED" -> refundFixedAmount;                     // 고정 수수료
            case "MIXED" -> originalAmount.multiply(refundFeeRate).add(refundFixedAmount);
            default -> BigDecimal.ZERO;
        };
    }

    public boolean refundable(LocalDateTime approvedAt, LocalDateTime publishedAt) {
        if (!refundable) {
            return false;
        }
        // 결제 승인 시간 이후 refund 가능 시간 계싼
        LocalDateTime refundDeadline =
                approvedAt.plusDays(refundPeriodDays.toDays());
        return !publishedAt.isAfter(refundDeadline);
    }
}

package com.smore.payment.payment.application.facade.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public record CancelPolicyResult(
        Duration cancelLimitMinutes,
        String cancelFeeType,
        BigDecimal cancelFeeRate,
        BigDecimal cancelFixedAmount,
        boolean cancellable
) {


    /**
     * 수수료 계산 메서드 — 필요 시 사용
     */
    public BigDecimal calculateCancelFee(BigDecimal originalAmount) {

        return switch (cancelFeeType.toUpperCase()) {
            case "RATE" -> originalAmount.multiply(cancelFeeRate); // 비율 수수료
            case "FIXED" -> cancelFixedAmount;                     // 고정 수수료
            case "MIXED" -> originalAmount.multiply(cancelFeeRate).add(cancelFixedAmount);
            default -> BigDecimal.ZERO;
        };
    }

    public boolean cancellable(LocalDateTime approvedAt, LocalDateTime publishedAt) {
        if (!cancellable) {
            return false;
        }
        // 결제 승인 시간 이후 cancel 가능 시간 계싼
        LocalDateTime cancelDeadline = approvedAt.plusMinutes(cancelLimitMinutes.toMinutes());
        return !publishedAt.isAfter(cancelDeadline);
    }
}

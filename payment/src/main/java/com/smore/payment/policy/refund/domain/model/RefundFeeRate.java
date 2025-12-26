package com.smore.payment.policy.refund.domain.model;

import java.math.BigDecimal;

public record RefundFeeRate(BigDecimal value) {

    public RefundFeeRate {
        if (value == null)
            throw new IllegalArgumentException("수수료율이 필요합니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("수수료율은 음수일 수 없습니다.");
        if (value.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("수수료율은 1(100%) 이하이어야 합니다.");
    }

    public static RefundFeeRate of(BigDecimal amount) {
        return new RefundFeeRate(amount);
    }
}


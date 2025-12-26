package com.smore.payment.policy.refund.domain.model;

import java.math.BigDecimal;

public record RefundFixedAmount(BigDecimal value) {

    public RefundFixedAmount {
        if (value == null)
            throw new IllegalArgumentException("정액 수수료가 필요합니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("정액 수수료는 음수일 수 없습니다.");
    }

    public static RefundFixedAmount of(BigDecimal amount) {
        return new RefundFixedAmount(amount);
    }
}

package com.smore.payment.policy.fee.domain.model;

import java.math.BigDecimal;

public record FixedAmount(BigDecimal value) {

    public FixedAmount {
        if (value == null)
            throw new IllegalArgumentException("정액 수수료가 필요합니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("정액 수수료는 음수일 수 없습니다.");
    }

    public static FixedAmount of(BigDecimal amount) {
        return new FixedAmount(amount);
    }
}

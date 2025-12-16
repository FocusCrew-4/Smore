package com.smore.payment.policy.fee.domain.model;

import java.math.BigDecimal;

public record FeeRate(BigDecimal value) {

    public FeeRate {
        if (value == null)
            throw new IllegalArgumentException("수수료율이 필요합니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("수수료율은 음수일 수 없습니다.");
        if (value.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("수수료율은 1(100%) 이하이어야 합니다.");
    }

    public static FeeRate of(BigDecimal amount) {
        return new FeeRate(amount);
    }
}


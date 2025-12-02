package com.smore.payment.cancelpolicy.domain.model;

import com.smore.payment.feepolicy.domain.model.FixedAmount;

import java.math.BigDecimal;

public record CancelFixedAmount(BigDecimal value) {

    public CancelFixedAmount {
        if (value == null)
            throw new IllegalArgumentException("정액 수수료가 필요합니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("정액 수수료는 음수일 수 없습니다.");
    }

    public static CancelFixedAmount of(BigDecimal amount) {
        return new CancelFixedAmount(amount);
    }
}

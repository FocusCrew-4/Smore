package com.smore.payment.cancelpolicy.domain.model;

import com.smore.payment.feepolicy.domain.model.FeeRate;

import java.math.BigDecimal;

public record CancelFeeRate(BigDecimal value) {

    public CancelFeeRate {
        if (value == null)
            throw new IllegalArgumentException("수수료율이 필요합니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("수수료율은 음수일 수 없습니다.");
        if (value.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("수수료율은 1(100%) 이하이어야 합니다.");
    }

    public static CancelFeeRate of(BigDecimal amount) {
        return new CancelFeeRate(amount);
    }
}


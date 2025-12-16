package com.smore.payment.payment.domain.service;

import java.math.BigDecimal;

public record FeePolicyDecision(
        String feeType,
        BigDecimal rate,
        BigDecimal fixedAmount
) {
}
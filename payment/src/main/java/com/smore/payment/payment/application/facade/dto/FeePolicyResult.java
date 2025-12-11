package com.smore.payment.payment.application.facade.dto;

import java.math.BigDecimal;

public record FeePolicyResult(
        String feeType,         // RATE / FIXED / MIXED
        BigDecimal rate,
        BigDecimal fixedAmount
) {}
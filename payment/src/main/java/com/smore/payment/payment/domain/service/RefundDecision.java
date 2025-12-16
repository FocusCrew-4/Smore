package com.smore.payment.payment.domain.service;

import java.math.BigDecimal;

public record RefundDecision(boolean refundable, BigDecimal refundAmount, String failureReason) {

    public static RefundDecision success(BigDecimal refundAmount) {
        return new RefundDecision(true, refundAmount, null);
    }

    public static RefundDecision failure(String failureReason) {
        return new RefundDecision(false, null, failureReason);
    }
}
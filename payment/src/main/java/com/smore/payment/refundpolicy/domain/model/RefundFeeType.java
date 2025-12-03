package com.smore.payment.refundpolicy.domain.model;

import lombok.Getter;

@Getter
public enum RefundFeeType {
    RATE("비율 수수료 (예: 5%)"),
    FIXED("고정 수수료 (예: 500원)"),
    MIXED("비율 + 고정 복합 수수료");

    private final String description;

    RefundFeeType(String description) {
        this.description = description;
    }

    public static RefundFeeType of(String value) {
        try {
            return RefundFeeType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FeeType: " + value);
        }
    }
}

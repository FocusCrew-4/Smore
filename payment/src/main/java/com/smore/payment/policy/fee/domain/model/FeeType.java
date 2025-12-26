package com.smore.payment.policy.fee.domain.model;

import lombok.Getter;

@Getter
public enum FeeType {
    RATE("비율 수수료 (예: 5%)"),
    FIXED("고정 수수료 (예: 500원)"),
    MIXED("비율 + 고정 복합 수수료");

    private final String description;

    FeeType(String description) {
        this.description = description;
    }

    public static FeeType of(String value) {
        try {
            return FeeType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FeeType: " + value);
        }
    }
}

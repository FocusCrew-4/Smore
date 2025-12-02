package com.smore.payment.feepolicy.domain.model;

public enum FeeType {
    RATE("비율 수수료 (예: 5%)"),
    FIXED("고정 수수료 (예: 500원)"),
    MIXED("비율 + 고정 복합 수수료");

    private final String description;

    FeeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static FeeType of(String value) {
        try {
            return FeeType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FeeType: " + value);
        }
    }
}

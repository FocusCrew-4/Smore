package com.smore.payment.cancelpolicy.domain.model;

import lombok.Getter;

@Getter
public enum CancelFeeType {
    RATE("비율 수수료 (예: 5%)"),
    FIXED("고정 수수료 (예: 500원)"),
    MIXED("비율 + 고정 복합 수수료");

    private final String description;

    CancelFeeType(String description) {
        this.description = description;
    }

    public static CancelFeeType of(String value) {
        try {
            return CancelFeeType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FeeType: " + value);
        }
    }
}

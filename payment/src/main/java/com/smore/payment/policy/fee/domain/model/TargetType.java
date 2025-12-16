package com.smore.payment.policy.fee.domain.model;

public enum TargetType {
    CATEGORY, MERCHANT, USER_TYPE;

    public static TargetType of(String value) {
        try {
            return TargetType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TargetType: " + value);
        }
    }
}

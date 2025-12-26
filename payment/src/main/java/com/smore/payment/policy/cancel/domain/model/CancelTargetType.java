package com.smore.payment.policy.cancel.domain.model;

public enum CancelTargetType {
    CATEGORY, MERCHANT, AUCTION_TYPE, USER_TYPE;

    public static CancelTargetType of(String value) {
        try {
            return CancelTargetType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TargetType: " + value);
        }
    }
}

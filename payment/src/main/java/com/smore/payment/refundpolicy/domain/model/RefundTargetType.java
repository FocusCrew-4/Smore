package com.smore.payment.refundpolicy.domain.model;

public enum RefundTargetType {
    CATEGORY, MERCHANT, AUCTION_TYPE, USER_TYPE;

    public static RefundTargetType of(String value) {
        try {
            return RefundTargetType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TargetType: " + value);
        }
    }
}

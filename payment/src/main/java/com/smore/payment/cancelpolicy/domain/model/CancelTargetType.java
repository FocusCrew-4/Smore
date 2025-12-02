package com.smore.payment.cancelpolicy.domain.model;

import com.smore.payment.feepolicy.domain.model.TargetType;

public enum CancelTargetType {
    CATEGORY, MERCHANT, AUCTION, COMPETITION, USER_TYPE;

    public static CancelTargetType of(String value) {
        try {
            return CancelTargetType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TargetType: " + value);
        }
    }
}

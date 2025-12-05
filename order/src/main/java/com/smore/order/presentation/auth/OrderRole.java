package com.smore.order.presentation.auth;

public enum OrderRole {
    NONE("비회원"),
    ADMIN("관리자"),
    SELLER("판매자"),
    CONSUMER("소비자")
    ;

    private final String description;

    OrderRole(String description) {
        this.description = description;
    }

    public String desc() { return description; }

    public boolean isNot(OrderRole role) {
        return this != role;
    }

    public boolean isNotAny(OrderRole... roles) {
        for (OrderRole role : roles) {
            if (this == role) {
                return false;
            }
        }
        return true;
    }

    public static OrderRole from(String value) {
        try {
            return OrderRole.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return OrderRole.NONE;
        }
    }
}
package com.smore.member.domain.enums;

public enum Role {
    ADMIN("관리자"),
    SELLER("판매자"),
    CONSUMER("소비자");

    private final String desc;

    Role(String desc) {
        this.desc = desc;
    }

    public String desc() {
        return desc;
    }
}

package com.smore.member.domain.enums;

public enum Role {
    NONE("비회원"),
    ADMIN("관리자"),
    SELLER("판매자"),
    CONSUMER("소비자"),

    USER("판매자 + 소비자 시스템 내부권한");

    private final String desc;

    Role(String desc) {
        this.desc = desc;
    }

    public String desc() {
        return desc;
    }
}

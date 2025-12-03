package com.smore.member.domain.enums;

public enum MemberStatus {
    ACTIVE("활성 계정"),
    INACTIVE("비활성 계정"),
    DELETED("삭제된 계정"),
    BANNED("정지된 계정");

    private final String desc;

    MemberStatus(String desc) {
        this.desc = desc;
    }

    public String desc() {
        return desc;
    }
}

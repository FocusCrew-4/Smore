package com.smore.member.application.service.command;

public record InfoUpdateCommand(
    Long requesterId,
    Long targetId,
    String nickname,
    String email,
    String password
) {
    public boolean hasNickname() {
        return this.nickname != null;
    }

    public boolean hasEmail() {
        return this.email != null;
    }

    public boolean hasPassword() {
        return this.password != null;
    }
}

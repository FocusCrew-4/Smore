package com.smore.member.application.service.command;

public record InfoUpdateCommand(
    Long requesterId,
    Long targetId,
    String nickname,
    String email,
    String password
) {

}

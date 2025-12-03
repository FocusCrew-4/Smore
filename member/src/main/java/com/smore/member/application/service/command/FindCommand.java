package com.smore.member.application.service.command;

public record FindCommand(
    Long requesterId,
    Long targetId
) {

}

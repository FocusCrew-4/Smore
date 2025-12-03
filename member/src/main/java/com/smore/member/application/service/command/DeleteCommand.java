package com.smore.member.application.service.command;

public record DeleteCommand(
    Long requesterId,
    Long targetId
) {

}

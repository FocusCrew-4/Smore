package com.smore.member.application.service.command;

public record LoginCommand(
    String email,
    String password
) {

}

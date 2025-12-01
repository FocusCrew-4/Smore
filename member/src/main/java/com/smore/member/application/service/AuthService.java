package com.smore.member.application.service;

import com.smore.member.application.service.command.LoginCommand;

public interface AuthService {

    String login(LoginCommand command);

}

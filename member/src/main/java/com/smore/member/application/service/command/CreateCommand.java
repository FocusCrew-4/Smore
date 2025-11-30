package com.smore.member.application.service.command;

import com.smore.member.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateCommand {
    final Role role;
    final String email;
    final String password;
    final String nickname;
}

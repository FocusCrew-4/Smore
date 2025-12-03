package com.smore.member.application.service.usecase;

import com.smore.member.application.service.command.FindCommand;
import com.smore.member.application.service.result.MemberResult;

public interface MemberFind extends RoleSupportable {

    MemberResult findMember(FindCommand findCommand);

}

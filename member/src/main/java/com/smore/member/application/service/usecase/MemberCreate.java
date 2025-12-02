package com.smore.member.application.service.usecase;

import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.result.MemberResult;

public interface MemberCreate {

    MemberResult createMember(CreateCommand command);

}

package com.smore.member.application.service.usecase;

import com.smore.member.application.service.command.InfoUpdateCommand;
import com.smore.member.application.service.result.MemberResult;

public interface MemberInfoUpdate {

    MemberResult update(InfoUpdateCommand command);

}

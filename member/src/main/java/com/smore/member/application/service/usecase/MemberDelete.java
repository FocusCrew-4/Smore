package com.smore.member.application.service.usecase;

import com.smore.member.application.service.command.DeleteCommand;
import com.smore.member.application.service.result.MemberResult;

public interface MemberDelete extends RoleSupportable {

    MemberResult delete(DeleteCommand deleteCommand);

}

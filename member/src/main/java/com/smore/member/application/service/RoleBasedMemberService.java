package com.smore.member.application.service;

import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.usecase.MemberCreate;
import com.smore.member.application.service.usecase.MemberRead;
import com.smore.member.application.service.usecase.RoleSupportable;

public interface RoleBasedMemberService
extends RoleSupportable, MemberCreate, MemberRead {

    @Override
    default MemberResult createMember(CreateCommand command) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    default MemberResult readMember() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

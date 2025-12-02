package com.smore.member.application.service.selector;

import com.smore.member.application.service.usecase.MemberDelete;
import com.smore.member.domain.enums.Role;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MemberDeleteSelector extends RoleSelector<MemberDelete> {

    public MemberDeleteSelector(List<MemberDelete> deleters) {
        super(deleters);
    }

    @Override
    public MemberDelete select(Role role) {
        return super.select(role);
    }
}

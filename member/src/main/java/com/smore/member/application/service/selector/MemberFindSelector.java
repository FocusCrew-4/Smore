package com.smore.member.application.service.selector;

import com.smore.member.application.service.usecase.MemberFind;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemberFindSelector extends RoleSelector<MemberFind>{

    public MemberFindSelector(List<MemberFind> finders) {
        super(finders);
    }

    @Override
    public MemberFind select(Role role) {
        return super.select(role);
    }
}

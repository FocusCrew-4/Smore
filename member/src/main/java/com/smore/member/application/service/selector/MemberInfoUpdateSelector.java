package com.smore.member.application.service.selector;

import com.smore.member.application.service.usecase.MemberInfoUpdate;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemberInfoUpdateSelector extends RoleSelector<MemberInfoUpdate>{

    public MemberInfoUpdateSelector(List<MemberInfoUpdate> finders) {
        super(finders);
    }

    @Override
    public MemberInfoUpdate select(Role role) {
        return super.select(role);
    }
}

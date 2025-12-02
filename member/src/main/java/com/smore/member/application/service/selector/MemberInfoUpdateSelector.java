package com.smore.member.application.service.selector;

import com.smore.member.application.service.usecase.MemberInfoUpdate;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemberInfoUpdateSelector {

    private final Map<Role, MemberInfoUpdate> infoUpdateMap = new EnumMap<>(Role.class);

    public MemberInfoUpdateSelector(List<MemberInfoUpdate> infoUpdaters) {
        for (MemberInfoUpdate updater : infoUpdaters) {
            if (updater instanceof RoleSupportable supportable) {
                infoUpdateMap.put(supportable.getSupportedRole(), updater);
            }
        }
    }

    public MemberInfoUpdate select(Role role) {
        if (role.equals(Role.SELLER) || role.equals(Role.CONSUMER)) {
            role = Role.USER;
        }
        if (!infoUpdateMap.containsKey(role)) {
            throw new RuntimeException("권한이 없습니다");
        }
        return infoUpdateMap.get(role);
    }
}

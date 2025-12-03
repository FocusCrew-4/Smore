package com.smore.member.application.service.selector;

import com.smore.member.application.service.usecase.MemberFind;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemberFindSelector {

    private final Map<Role, MemberFind> readerMap = new EnumMap<>(Role.class);

    public MemberFindSelector(List<MemberFind> readers) {
        for (MemberFind reader : readers) {
            if (reader instanceof RoleSupportable supportable) {
                readerMap.put(supportable.getSupportedRole(), reader);
            }
        }
    }

    public MemberFind select(Role role) {
        if (role.equals(Role.SELLER) || role.equals(Role.CONSUMER)) {
            role = Role.USER;
        }
        if (!readerMap.containsKey(role)) {
            throw new RuntimeException("권한이 없습니다");
        }
        return readerMap.get(role);
    }
}

package com.smore.member.application.service.selector;

import com.smore.member.application.service.usecase.MemberRead;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MemberReadSelector {

    private final Map<Role, MemberRead> readerMap = new EnumMap<>(Role.class);

    public MemberReadSelector(List<MemberRead> readers) {
        for (MemberRead reader : readers) {
            if (reader instanceof RoleSupportable supportable) {
                readerMap.put(supportable.getSupportedRole(), reader);
            }
        }
    }

    public MemberRead select(Role role) {
        if (role.equals(Role.SELLER) || role.equals(Role.CONSUMER)) {
            role = Role.USER;
        }
        return readerMap.get(role);
    }
}

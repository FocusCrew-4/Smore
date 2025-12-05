package com.smore.member.application.service.selector;

import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class RoleSelector<T extends RoleSupportable> {

    private final Map<Role, T> roleMap = new EnumMap<>(Role.class);

    protected RoleSelector(List<T> handlers) {
        for (T handler : handlers) {
            roleMap.put(handler.getSupportedRole(), handler);
        }
    }

    protected T select(Role role) {
        role = role.normalizeToUser();
        if (!roleMap.containsKey(role)) {
            throw new RuntimeException("권한이 없습니다");
        }
        return roleMap.get(role);
    }

}

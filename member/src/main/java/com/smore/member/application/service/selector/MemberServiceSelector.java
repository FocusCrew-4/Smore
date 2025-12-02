package com.smore.member.application.service.selector;

import com.smore.member.application.service.RoleBasedMemberService;
import com.smore.member.domain.enums.Role;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceSelector {

    private final Map<Role, RoleBasedMemberService> serviceMap = new EnumMap<>(Role.class);

    public MemberServiceSelector(List<RoleBasedMemberService> services) {
        for (RoleBasedMemberService service : services) {
            serviceMap.put(service.getSupportedRole(), service);
        }
    }

    public RoleBasedMemberService select(Role role) {
        return serviceMap.get(role);
    }
}

package com.smore.member.infrastructure.security.userdetails;

import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.model.Member;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public  CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public String getUsername() {
        return member.getCredential().email();
    }

    @Override
    public String getPassword() {
        return member.getCredential().password();
    }

    public Long getUserId() {
        return member.getId();
    }

    public String getRole() {
        return member.getRole().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + member.getRole().name())
        );
    }

    @Override
    public boolean isEnabled() {
        return member.getStatus() == MemberStatus.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}

package com.smore.member.infrastructure.jwt;

import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import com.smore.member.infrastructure.security.userdetails.CustomUserDetails;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements
    Converter<Jwt, AbstractAuthenticationToken> {

    private final MemberRepository memberRepository;
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // JWT 클레임에서 사용자 식별자 추출
        String subject = jwt.getSubject();

        Long memberId;
        try {
            memberId = Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid member id: " + subject, e);
        }

        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new UsernameNotFoundException("Member not found: " + memberId);
        }

        CustomUserDetails userDetails = new CustomUserDetails(member);

        // 권한 변환
        Collection<GrantedAuthority> grantedAuthorities
            = jwtGrantedAuthoritiesConverter.convert(jwt);

        return new UsernamePasswordAuthenticationToken(userDetails, jwt, grantedAuthorities);
    }
}

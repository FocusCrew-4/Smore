package com.smore.member.infrastructure.adapter.in;

import com.smore.member.application.port.in.MemberJwtVerify;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberJwtVerifyImpl implements MemberJwtVerify {

    private final JwtDecoder jwtDecoder;
    private final MemberRepository memberRepository;

    @Override
    public boolean verify(String token) {
        try {
            Jwt jwt1 = jwtDecoder.decode(token.replace("Bearer ", ""));
            Long memberId = Long.valueOf(jwt1.getSubject());
            Member member = memberRepository.findById(memberId);
            if (!member.isSameRole(Role.valueOf(jwt1.getClaim("role")))) {
                return false;
            }
        } catch (JwtException e) {
            log.error("Jwt exception", e);
            return false;
        } catch (NoSuchElementException e) {
            log.error("유효한 회원이 없습니다", e);
            return false;
        }
        return true;
    }
}

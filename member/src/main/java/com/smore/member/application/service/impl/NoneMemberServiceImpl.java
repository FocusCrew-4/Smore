package com.smore.member.application.service.impl;

import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.mapper.MemberMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class NoneMemberServiceImpl extends AbstractMemberService {

    private final PasswordEncoder passwordEncoder;

    public NoneMemberServiceImpl(
        MemberRepository memberRepository,
        MemberMapper mapper,
        PasswordEncoder passwordEncoder
    ) {
        super(memberRepository, mapper);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Role getSupportedRole() {
        return Role.NONE;
    }

    @Override
    public MemberResult createMember(CreateCommand command) {

        Member member = Member.create(
            command.getRole(),
            command.getEmail(),
            passwordEncoder.encode(command.getPassword()),
            command.getNickname()
        );

        Member savedMember = memberRepository.save(member);

        return mapper.toMemberResult(savedMember);
    }
}

package com.smore.member.application.service.impl.member;

import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.mapper.MemberAppMapper;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.usecase.MemberCreate;
import com.smore.member.application.service.usecase.RoleSupportable;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoneServiceImpl implements MemberCreate, RoleSupportable {

    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final MemberAppMapper mapper;
    private final Clock clock;


    @Override
    public Role getSupportedRole() {
        return Role.NONE;
    }

    @Override
    @Transactional
    public MemberResult createMember(CreateCommand command) {
        if (repository.findByEmail(command.getEmail()) != null) {
            throw new RuntimeException("email already exists");
        }

        Member member = Member.create(
            command.getRole(),
            command.getEmail(),
            passwordEncoder.encode(command.getPassword()),
            command.getNickname(),
            clock
        );

        Member savedMember = repository.save(member);

        return mapper.toMemberResult(savedMember);
    }
}

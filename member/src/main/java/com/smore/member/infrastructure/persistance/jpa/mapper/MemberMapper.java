package com.smore.member.infrastructure.persistance.jpa.mapper;

import com.smore.member.domain.model.Member;
import com.smore.member.domain.vo.Credential;
import com.smore.member.infrastructure.persistance.jpa.entity.MemberJpa;
import com.smore.member.infrastructure.persistance.jpa.vo.CredentialEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member toDomain(MemberJpa entity);

    MemberJpa toEntity(Member member);

    Credential toDomain(CredentialEmbeddable credential);

    CredentialEmbeddable toEntity(Credential credential);
}

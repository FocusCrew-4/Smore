package com.smore.member.infrastructure.persistence.jpa.mapper;

import com.smore.member.domain.model.Member;
import com.smore.member.domain.vo.Credential;
import com.smore.member.infrastructure.persistence.jpa.entity.MemberJpa;
import com.smore.member.infrastructure.persistence.jpa.vo.CredentialEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberJpaMapper {
    Member toDomain(MemberJpa entity);

    MemberJpa toEntity(Member member);

    Credential toDomain(CredentialEmbeddable credential);

    CredentialEmbeddable toEntity(Credential credential);
}

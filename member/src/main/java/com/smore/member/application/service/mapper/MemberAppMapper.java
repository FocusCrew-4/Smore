package com.smore.member.application.service.mapper;

import com.smore.member.application.service.result.MemberResult;
import com.smore.member.domain.model.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberAppMapper {

    public MemberResult toMemberResult(Member member) {
        return new MemberResult(
            member.getId(),
            member.getRole(),
            member.getCredential().email(),
            member.getNickname(),
            member.getAuctionCancelCount(),
            member.getStatus(),
            member.getCreatedAt(),
            member.getUpdatedAt(),
            member.getDeletedAt(),
            member.getDeletedBy()
        );
    }

}

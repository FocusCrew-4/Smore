package com.smore.member.presentation.web.dto.response;

import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;

public record FindResponseDto(
    Long id,
    Role role,
    String email,
    String nickname,
    Integer auctionCancelCount,
    MemberStatus memberStatus,
    Long deletedBy
) {

}

package com.smore.member.application.service.result;

import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;
import java.time.LocalDateTime;

public record MemberResult(
    Long id,
    Role role,
    String email,
    String nickname,
    Integer auctionCancelCount,
    MemberStatus memberStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    Long deletedBy
) {
}

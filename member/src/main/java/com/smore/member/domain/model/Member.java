package com.smore.member.domain.model;

import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.vo.Credential;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Member {
    private final Long id;
    private final Role role;
    private Credential credential;
    private String nickname;
    private Integer auctionCancelCount;
    private MemberStatus status;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
}
package com.smore.member.domain.model;

import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;
import com.smore.member.domain.vo.Credential;
import java.time.Clock;
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

    public static Member create(
        Role role,
        String email,
        String password,
        String nickname,
        Clock clock
    ) {
        LocalDateTime now = LocalDateTime.now(clock);
        return new Member(
            null,
            role,
            new Credential(email, password),
            nickname,
            0,
            MemberStatus.ACTIVE,
            now,
            now,
            null,
            null
        );
    }

    public void updateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("nickname cannot be null or empty");
        }
        this.nickname = nickname;
    }

    public void changeEmail(String email) {
        this.credential
            = new Credential(email, this.credential.password());
    }

    public void changePassword(String password) {
        this.credential
            = new Credential(this.credential.email(), password);
    }

    public void deleteMember(Long requesterId) {
        this.deletedAt = LocalDateTime.now(Clock.systemUTC());
        this.status = MemberStatus.DELETED;
        this.deletedBy = requesterId;
    }

    public boolean isMe(Long memberId) {
        return this.id.equals(memberId);
    }
}

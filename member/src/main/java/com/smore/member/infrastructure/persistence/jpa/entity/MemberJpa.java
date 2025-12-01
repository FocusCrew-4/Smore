package com.smore.member.infrastructure.persistence.jpa.entity;

import com.smore.member.domain.enums.Role;
import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.infrastructure.persistence.jpa.vo.CredentialEmbeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "email", column = @Column(name = "email", unique = true, nullable = false)),
        @AttributeOverride(name = "password", column = @Column(name = "password", nullable = false))
    })
    private CredentialEmbeddable credential;
    private String nickname;
    private Integer auctionCancelCount;
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
}

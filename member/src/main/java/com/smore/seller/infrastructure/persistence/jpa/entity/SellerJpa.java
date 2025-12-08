package com.smore.seller.infrastructure.persistence.jpa.entity;

import com.smore.seller.domain.enums.SellerStatus;
import com.smore.seller.infrastructure.persistence.jpa.vo.MoneyEmbeddable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "p_seller",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_p_seller_member_id",
            columnNames = {"member_id"}
        )
    })
public class SellerJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private Long memberId;
    private String accountNum;
    @Enumerated(EnumType.STRING)
    private SellerStatus status;
    @Embedded
    private MoneyEmbeddable money;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
}

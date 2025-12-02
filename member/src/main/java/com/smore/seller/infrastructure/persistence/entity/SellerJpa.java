package com.smore.seller.infrastructure.persistence.entity;

import com.smore.seller.domain.enums.SellerStatus;
import com.smore.seller.infrastructure.persistence.vo.MoneyEmbeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "p_seller")
public class SellerJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
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

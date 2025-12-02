package com.smore.payment.feepolicy.infrastructure.persistence.model;

import com.smore.payment.feepolicy.domain.model.FeeType;
import com.smore.payment.feepolicy.domain.model.TargetType;
import com.smore.payment.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "fee_policies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FeePolicyEntity extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, updatable = false)
    private TargetType targetType;

    @Column(name = "target_key", nullable = false, updatable = false)
    private UUID targetKey; // 카테고리ID, 판메자ID

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, updatable = false)
    private FeeType feeType;

    @Embedded
    private FeeRateJpa rate;

    @Embedded
    private FixedAmountJpa fixedAmount;

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;

    public FeePolicyEntity(
            UUID id,
            TargetType targetType,
            UUID targetKey,
            FeeType feeType,
            FeeRateJpa rate,
            FixedAmountJpa fixedAmount,
            boolean active
    ) {
        this.id = id;
        this.targetType = targetType;
        this.targetKey = targetKey;
        this.feeType = feeType;
        this.rate = rate;
        this.fixedAmount = fixedAmount;
        this.active = active;
    }

}

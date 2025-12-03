package com.smore.payment.cancelpolicy.infrastructure.persistence.model;

import com.smore.payment.cancelpolicy.domain.model.CancelFeeType;
import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import com.smore.payment.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cancel_policies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CancelPolicyEntity extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_target_type", nullable = false, updatable = false)
    private CancelTargetType cancelTargetType;

    @Column(name = "target_key", nullable = false, updatable = false)
    private UUID targetKey;

    @Column(name = "cancel_limit_minutes")
    private Integer cancelLimitMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_fee_type", nullable = false, updatable = false)
    private CancelFeeType cancelFeeType;

    @Embedded
    private CancelFeeRateJpa cancelFeeRateJpa;

    @Embedded
    private CancelFixedAmountJpa cancelFixedAmountJpa;

    @Column(name = "cancellable", nullable = false, updatable = false)
    private boolean cancellable;

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;

    public CancelPolicyEntity(
            UUID id,
            CancelTargetType cancelTargetType,
            UUID targetKey,
            Integer cancelLimitMinutes,
            CancelFeeType cancelFeeType,
            CancelFeeRateJpa rate,
            CancelFixedAmountJpa fixedAmount,
            boolean cancellable,
            boolean active
    ) {
        this.id = id;
        this.cancelTargetType = cancelTargetType;
        this.targetKey = targetKey;
        this.cancelLimitMinutes = cancelLimitMinutes;
        this.cancelFeeType = cancelFeeType;
        this.cancelFeeRateJpa = rate;
        this.cancelFixedAmountJpa = fixedAmount;
        this.cancellable = cancellable;
        this.active = active;
    }

}

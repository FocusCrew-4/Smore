package com.smore.payment.refundpolicy.infrastructure.persistence.model;

import com.smore.payment.global.entity.BaseEntity;
import com.smore.payment.refundpolicy.domain.model.RefundFeeType;
import com.smore.payment.refundpolicy.domain.model.RefundTargetType;
import com.smore.payment.refundpolicy.domain.model.TargetKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "refund_policies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefundPolicyEntity extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_target_type", nullable = false, updatable = false)
    private RefundTargetType refundTargetType;

    @Column(name = "target_key", nullable = false, updatable = false)
    private String targetKey;

    @Column(name = "refund_period_days")
    private Duration refundPeriodDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_fee_type", nullable = false, updatable = false)
    private RefundFeeType refundFeeType;

    @Embedded
    private RefundFeeRateJpa refundFeeRateJpa;

    @Embedded
    private RefundFixedAmountJpa refundFixedAmountJpa;

    @Column(name = "refundable", nullable = false, updatable = false)
    private boolean refundable;

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;

    public RefundPolicyEntity(
            UUID id,
            RefundTargetType refundTargetType,
            String targetKey,
            Duration refundPeriodDays,
            RefundFeeType refundFeeType,
            RefundFeeRateJpa rate,
            RefundFixedAmountJpa fixedAmount,
            boolean refundable,
            boolean active
    ) {
        this.id = id;
        this.refundTargetType = refundTargetType;
        this.targetKey = targetKey;
        this.refundPeriodDays = refundPeriodDays;
        this.refundFeeType = refundFeeType;
        this.refundFeeRateJpa = rate;
        this.refundFixedAmountJpa = fixedAmount;
        this.refundable = refundable;
        this.active = active;
    }

}

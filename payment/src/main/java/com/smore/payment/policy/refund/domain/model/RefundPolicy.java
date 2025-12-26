package com.smore.payment.policy.refund.domain.model;

import java.time.Duration;
import java.util.UUID;

public class RefundPolicy {

    private final UUID id;
    private final RefundTargetType refundTargetType;
    private final TargetKey targetKey;
    private final Duration refundPeriodDays;
    private final RefundFeeType refundFeeType;
    private final RefundFeeRate refundFeeRate;
    private final RefundFixedAmount refundFixedAmount;
    private final boolean refundable;
    private boolean active;

    protected RefundPolicy(
        RefundTargetType refundTargetType,
        TargetKey targetKey,
        Duration refundPeriodDays,
        RefundFeeType refundFeeType,
        RefundFeeRate refundFeeRate,
        RefundFixedAmount refundFixedAmount,
        boolean refundable
    ) {
        validate(refundFeeType, refundFeeRate, refundFixedAmount);

        this.id = UUID.randomUUID();
        this.refundTargetType = refundTargetType;
        this.targetKey = targetKey;
        this.refundPeriodDays = refundPeriodDays;
        this.refundFeeType = refundFeeType;
        this.refundFeeRate = refundFeeRate;
        this.refundFixedAmount = refundFixedAmount;
        this.refundable = refundable;

        active = false;
    }

    public static RefundPolicy create(
            RefundTargetType refundTargetType,
            TargetKey targetKey,
            Duration refundPeriodDays,
            RefundFeeType feeType,
            RefundFeeRate rate,
            RefundFixedAmount refundFixedAmount,
            boolean refundable
    ) {
        return new RefundPolicy(refundTargetType, targetKey, refundPeriodDays, feeType, rate, refundFixedAmount,  refundable);
    }

    protected RefundPolicy(
            UUID id,
            RefundTargetType refundTargetType,
            TargetKey targetKey,
            Duration refundPeriodDays,
            RefundFeeType refundFeeType,
            RefundFeeRate refundFeeRate,
            RefundFixedAmount refundFixedAmount,
            boolean refundable,
            boolean active
    ) {
        this.id = id;
        this.refundTargetType = refundTargetType;
        this.targetKey = targetKey;
        this.refundPeriodDays = refundPeriodDays;
        this.refundFeeType = refundFeeType;
        this.refundFeeRate = refundFeeRate;
        this.refundFixedAmount = refundFixedAmount;
        this.refundable = refundable;
        this.active = active;
    }

    public static RefundPolicy reconstruct(
            UUID id,
            RefundTargetType refundTargetType,
            TargetKey targetKey,
            Duration refundPeriodDays,
            RefundFeeType feeType,
            RefundFeeRate rate,
            RefundFixedAmount refundFixedAmount,
            boolean refundable,
            boolean active
    ) {
        return new RefundPolicy(
                id, refundTargetType, targetKey, refundPeriodDays, feeType, rate, refundFixedAmount, refundable,
                active
        );
    }

    public UUID getId() { return id; }
    public RefundTargetType getRefundTargetType() { return refundTargetType; }
    public TargetKey getTargetKey() { return targetKey; }
    public Duration getRefundPeriodDays() { return refundPeriodDays; }
    public RefundFeeType getRefundFeeType() { return refundFeeType; }
    public RefundFeeRate getRefundFeeRate() { return refundFeeRate; }
    public RefundFixedAmount getRefundFixedAmount() { return refundFixedAmount; }
    public boolean isRefundable() { return refundable; }
    public boolean isActive() { return active; }

    private void validate(RefundFeeType refundFeeType, RefundFeeRate refundFeeRate, RefundFixedAmount refundFixedAmount) {
        switch (refundFeeType) {
            case RATE -> {
                if (refundFeeRate == null)
                    throw new IllegalArgumentException("정률 수수료(rate)가 반드시 필요합니다.");
            }
            case FIXED -> {
                if (refundFixedAmount == null)
                    throw new IllegalArgumentException("정액 수수료(fixedAmount)가 반드시 필요합니다.");
            }
            case MIXED -> {
                if (refundFeeRate == null || refundFixedAmount == null)
                    throw new IllegalArgumentException("혼합 수수료는 정률(rate)과 정액(fixed)이 모두 필요합니다.");
            }
        }
    }

    // 정책 활성화
    public void activate() {
        this.active = true;
    }

    // 정책 비활성화
    public void deactivate() {
        this.active = false;
    }

}

package com.smore.payment.feepolicy.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FeePolicy {

    private final UUID id;
    private final TargetType targetType;
    private final UUID targetKey; // 카테고리ID, 판매자ID
    private final FeeType feeType;
    private final FeeRate rate;
    private final FixedAmount fixedAmount;

    private boolean active;

    protected FeePolicy(
        TargetType targetType,
        UUID targetKey,
        FeeType feeType,
        FeeRate rate,
        FixedAmount fixedAmount
    ) {
        validate(feeType, rate, fixedAmount);

        this.id = UUID.randomUUID();
        this.targetType = targetType;
        this.targetKey = targetKey;
        this.feeType = feeType;
        this.rate = rate;
        this.fixedAmount = fixedAmount;

        active = false;
    }

    public static FeePolicy create(
            TargetType targetType,
            UUID targetKey,
            FeeType feeType,
            FeeRate rate,
            FixedAmount fixedAmount
    ) {
        return new FeePolicy(targetType, targetKey, feeType, rate, fixedAmount);
    }

    protected FeePolicy(
            UUID id,
            TargetType targetType,
            UUID targetKey,
            FeeType feeType,
            FeeRate rate,
            FixedAmount fixedAmount,
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

    public static FeePolicy reconstruct(
            UUID id,
            TargetType targetType,
            UUID targetKey,
            FeeType feeType,
            FeeRate rate,
            FixedAmount fixedAmount,
            boolean active
    ) {
        return new FeePolicy(
                id, targetType, targetKey, feeType, rate, fixedAmount,
                active
        );
    }

    public UUID getId() { return id; }
    public TargetType getTargetType() { return targetType; }
    public UUID getTargetKey() { return targetKey; }
    public FeeType getFeeType() { return feeType; }
    public FeeRate getRate() { return rate; }
    public FixedAmount getFixedAmount() { return fixedAmount; }
    public boolean isActive() { return active; }

    private void validate(FeeType feeType, FeeRate feeRate, FixedAmount fixedAmount) {
        switch (feeType) {
            case RATE -> {
                if (feeRate == null)
                    throw new IllegalArgumentException("정률 수수료(rate)가 반드시 필요합니다.");
            }
            case FIXED -> {
                if (fixedAmount == null)
                    throw new IllegalArgumentException("정액 수수료(fixedAmount)가 반드시 필요합니다.");
            }
            case MIXED -> {
                if (feeRate == null || fixedAmount == null)
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

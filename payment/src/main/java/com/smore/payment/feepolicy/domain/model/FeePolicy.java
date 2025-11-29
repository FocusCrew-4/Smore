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

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;

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
        createdAt = LocalDateTime.now();
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
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt,
            Long createdBy,
            Long updatedBy,
            Long deletedBy
    ) {
        this.id = id;
        this.targetType = targetType;
        this.targetKey = targetKey;
        this.feeType = feeType;
        this.rate = rate;
        this.fixedAmount = fixedAmount;

        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    public static FeePolicy reconstruct(
            UUID id,
            TargetType targetType,
            UUID targetKey,
            FeeType feeType,
            FeeRate rate,
            FixedAmount fixedAmount,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt,
            Long createdBy,
            Long updatedBy,
            Long deletedBy
    ) {
        return new FeePolicy(
                id, targetType, targetKey, feeType, rate, fixedAmount,
                active, createdAt, updatedAt, deletedAt,
                createdBy, updatedBy, deletedBy
        );
    }

    public UUID getId() { return id; }
    public TargetType getTargetType() { return targetType; }
    public UUID getTargetKey() { return targetKey; }
    public FeeType getFeeType() { return feeType; }
    public FeeRate getRate() { return rate; }
    public FixedAmount getFixedAmount() { return fixedAmount; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }
    public Long getDeletedBy() { return deletedBy; }

    private void validate(FeeType feeType, FeeRate rate, FixedAmount fixedAmount) {
        switch (feeType) {
            case RATE -> {
                if (rate == null)
                    throw new IllegalArgumentException("정률 수수료(rate)가 반드시 필요합니다.");
            }
            case FIXED -> {
                if (fixedAmount == null)
                    throw new IllegalArgumentException("정액 수수료(fixedAmount)가 반드시 필요합니다.");
            }
            case MIXED -> {
                if (rate == null || fixedAmount == null)
                    throw new IllegalArgumentException("혼합 수수료는 정률(rate)과 정액(fixed)이 모두 필요합니다.");
            }
        }
    }


    public BigDecimal calculateFee(BigDecimal amount) {
        return switch (feeType) {
            case RATE -> rate.apply(amount);
            case FIXED -> fixedAmount.value();
            case MIXED -> rate.apply(amount).add(fixedAmount.value());
        };
    }

    // 정책 활성화
    public void activate() {
        this.active = true;
    }

    // 정책 비활성화
    public void deActivate() {
        this.active = false;
    }

    public void deactivate(Long userId) {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId;
    }
}

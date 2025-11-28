package com.smore.payment.feepolicy.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FeePolicy {

    private final UUID id;
    private final TargetType targetType;
    private final UUID targetKey; // 상품ID, 카테고리ID, 판메자ID
    private final FeeType feeType;
    private final FeeRate rate;
    private final FixedAmount fixedAmount;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;

    public FeePolicy(
        UUID id,
        TargetType targetType,
        UUID targetKey,
        FeeType feeType,
        FeeRate rate,
        FixedAmount fixedAmount
    ) {
        validate(feeType, rate, fixedAmount);

        this.id = id;
        this.targetType = targetType;
        this.targetKey = targetKey;
        this.feeType = feeType;
        this.rate = rate;
        this.fixedAmount = fixedAmount;

        active = true;
        createdAt = LocalDateTime.now();
    }

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

    public void deactivate(Long userId) {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId;
    }
}

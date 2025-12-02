package com.smore.payment.cancelpolicy.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class CancelPolicy {

    private final UUID id;
    private final CancelTargetType cancelTargetType;
    private final UUID targetKey;
    private final Integer cancelLimitMinutes;
    private final CancelFeeType cancelFeeType;
    private final CancelFeeRate cancelFeeRate;
    private final CancelFixedAmount cancelFixedAmount;
    private final boolean cancellable;
    private boolean active;

    protected CancelPolicy(
        CancelTargetType cancelTargetType,
        UUID targetKey,
        Integer cancelLimitMinutes,
        CancelFeeType cancelFeeType,
        CancelFeeRate cancelFeeRate,
        CancelFixedAmount cancelFixedAmount,
        boolean cancellable
    ) {
        validate(cancelFeeType, cancelFeeRate, cancelFixedAmount);

        this.id = UUID.randomUUID();
        this.cancelTargetType = cancelTargetType;
        this.targetKey = targetKey;
        this.cancelLimitMinutes = cancelLimitMinutes;
        this.cancelFeeType = cancelFeeType;
        this.cancelFeeRate = cancelFeeRate;
        this.cancelFixedAmount = cancelFixedAmount;
        this.cancellable = cancellable;

        active = false;
    }

    public static CancelPolicy create(
            CancelTargetType cancelTargetType,
            UUID targetKey,
            Integer cancelLimitMinutes,
            CancelFeeType feeType,
            CancelFeeRate rate,
            CancelFixedAmount cancelFixedAmount,
            boolean cancellable
    ) {
        return new CancelPolicy(cancelTargetType, targetKey, cancelLimitMinutes, feeType, rate, cancelFixedAmount,  cancellable);
    }

    protected CancelPolicy(
            UUID id,
            CancelTargetType cancelTargetType,
            UUID targetKey,
            Integer cancelLimitMinutes,
            CancelFeeType cancelFeeType,
            CancelFeeRate cancelFeeRate,
            CancelFixedAmount cancelFixedAmount,
            boolean cancellable,
            boolean active
    ) {
        this.id = id;
        this.cancelTargetType = cancelTargetType;
        this.targetKey = targetKey;
        this.cancelLimitMinutes = cancelLimitMinutes;
        this.cancelFeeType = cancelFeeType;
        this.cancelFeeRate = cancelFeeRate;
        this.cancelFixedAmount = cancelFixedAmount;
        this.cancellable = cancellable;
        this.active = active;
    }

    public static CancelPolicy reconstruct(
            UUID id,
            CancelTargetType cancelTargetType,
            UUID targetKey,
            Integer cancelLimitMinutes,
            CancelFeeType feeType,
            CancelFeeRate rate,
            CancelFixedAmount cancelFixedAmount,
            boolean cancellable,
            boolean active
    ) {
        return new CancelPolicy(
                id, cancelTargetType, targetKey, cancelLimitMinutes, feeType, rate, cancelFixedAmount, cancellable,
                active
        );
    }

    public UUID getId() { return id; }
    public CancelTargetType getCancelTargetType() { return cancelTargetType; }
    public UUID getTargetKey() { return targetKey; }
    public Integer getCancelLimitMinutes() { return cancelLimitMinutes; }
    public CancelFeeType getCancelFeeType() { return cancelFeeType; }
    public CancelFeeRate getCancelFeeRate() { return cancelFeeRate; }
    public CancelFixedAmount getCancelFixedAmount() { return cancelFixedAmount; }
    public boolean isCancellable() { return cancellable; }
    public boolean isActive() { return active; }

    private void validate(CancelFeeType cancelFeeType, CancelFeeRate cancelFeeRate, CancelFixedAmount cancelFixedAmount) {
        switch (cancelFeeType) {
            case RATE -> {
                if (cancelFeeRate == null)
                    throw new IllegalArgumentException("정률 수수료(rate)가 반드시 필요합니다.");
            }
            case FIXED -> {
                if (cancelFixedAmount == null)
                    throw new IllegalArgumentException("정액 수수료(fixedAmount)가 반드시 필요합니다.");
            }
            case MIXED -> {
                if (cancelFeeRate == null || cancelFixedAmount == null)
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

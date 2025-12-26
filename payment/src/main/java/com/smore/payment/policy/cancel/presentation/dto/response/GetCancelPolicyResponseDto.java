package com.smore.payment.policy.cancel.presentation.dto.response;

import com.smore.payment.policy.cancel.domain.model.CancelPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetCancelPolicyResponseDto {

    private UUID id;
    private String targetType;
    private String targetKey;
    private Duration cancelLimitMinutes;
    private String feeType;
    private BigDecimal rate;
    private BigDecimal fixedAmount;
    private boolean cancelable;
    private boolean active;

    public static GetCancelPolicyResponseDto from(CancelPolicy cancelPolicy) {
        return new GetCancelPolicyResponseDto(
                cancelPolicy.getId(),
                cancelPolicy.getCancelTargetType().toString(),
                cancelPolicy.getTargetKey().toString(),
                cancelPolicy.getCancelLimitMinutes(),
                cancelPolicy.getCancelFeeType().toString(),
                cancelPolicy.getCancelFeeRate().value(),
                cancelPolicy.getCancelFixedAmount().value(),
                cancelPolicy.isCancellable(),
                cancelPolicy.isActive()
        );
    }
}

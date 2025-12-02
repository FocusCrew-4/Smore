package com.smore.payment.cancelpolicy.presentation.dto.response;

import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.presentation.dto.request.GetCancelPolicyRequestDto;
import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.presentation.dto.response.GetFeePolicyResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetCancelPolicyResponseDto {

    private UUID id;
    private String targetType;
    private UUID targetKey;
    private Integer cancelLimitMinutes;
    private String feeType;
    private BigDecimal rate;
    private BigDecimal fixedAmount;
    private boolean cancelable;
    private boolean active;

    public static GetCancelPolicyResponseDto from(CancelPolicy cancelPolicy) {
        return new GetCancelPolicyResponseDto(
                cancelPolicy.getId(),
                cancelPolicy.getCancelTargetType().toString(),
                cancelPolicy.getTargetKey(),
                cancelPolicy.getCancelLimitMinutes(),
                cancelPolicy.getCancelFeeType().toString(),
                cancelPolicy.getCancelFeeRate().value(),
                cancelPolicy.getCancelFixedAmount().value(),
                cancelPolicy.isCancellable(),
                cancelPolicy.isActive()
        );
    }
}

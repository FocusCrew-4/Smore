package com.smore.payment.cancelpolicy.application.command;

import com.smore.payment.cancelpolicy.domain.model.CancelFeeRate;
import com.smore.payment.cancelpolicy.domain.model.CancelFeeType;
import com.smore.payment.cancelpolicy.domain.model.CancelFixedAmount;
import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import com.smore.payment.cancelpolicy.presentation.dto.request.CreateCancelPolicyRequestDto;

import java.util.UUID;

public record CreateCancelPolicyCommand(
        CancelTargetType cancelTargetType,
        UUID targetKey,
        Integer cancelLimitMinutes,
        CancelFeeType cancelFeeType,
        CancelFeeRate rate,
        CancelFixedAmount cancelFixedAmount,
        boolean cancellable
) {
    public static CreateCancelPolicyCommand from(CreateCancelPolicyRequestDto createCancelPolicyRequestDto) {
        return new CreateCancelPolicyCommand(
                CancelTargetType.of(createCancelPolicyRequestDto.getCancelTargetType()),
                createCancelPolicyRequestDto.getTargetKey(),
                createCancelPolicyRequestDto.getCancelLimitMinutes(),
                CancelFeeType.of(createCancelPolicyRequestDto.getCancelFeeType()),
                CancelFeeRate.of(createCancelPolicyRequestDto.getRate()),
                CancelFixedAmount.of(createCancelPolicyRequestDto.getCancelFixedAmount()),
                createCancelPolicyRequestDto.isCancellable()
        );
    }
}

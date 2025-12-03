package com.smore.payment.cancelpolicy.application.query;

import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import com.smore.payment.cancelpolicy.presentation.dto.request.GetCancelPolicyRequestDto;

import java.util.UUID;

public record GetCancelPolicyQuery(
        CancelTargetType cancelTargetType,
        UUID targetKey
) {
    public static GetCancelPolicyQuery from(GetCancelPolicyRequestDto getCancelPolicyRequestDto) {
        return new GetCancelPolicyQuery(
                CancelTargetType.of(getCancelPolicyRequestDto.getCancelTargetType()),
                getCancelPolicyRequestDto.getTargetKey()
        );
    }
}

package com.smore.payment.feepolicy.application.query;

import com.smore.payment.feepolicy.domain.model.TargetType;
import com.smore.payment.feepolicy.presentation.dto.request.GetFeePolicyRequestDto;

import java.util.UUID;

public record GetFeePolicyQuery(
        TargetType targetType,
        UUID TargetKey
) {
    public static GetFeePolicyQuery from(GetFeePolicyRequestDto getFeePolicyRequestDto) {
        return new GetFeePolicyQuery(
                TargetType.of(getFeePolicyRequestDto.getTargetType()),
                getFeePolicyRequestDto.getTargetKey()
        );
    }
}

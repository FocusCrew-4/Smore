package com.smore.payment.feepolicy.application.command;

import com.smore.payment.feepolicy.domain.model.FeeType;
import com.smore.payment.feepolicy.domain.model.FeeRate;
import com.smore.payment.feepolicy.domain.model.FixedAmount;
import com.smore.payment.feepolicy.domain.model.TargetType;
import com.smore.payment.feepolicy.presentation.dto.request.CreateFeePolicyRequestDto;

import java.util.UUID;

public record CreateFeePolicyCommand(
        TargetType targetType,
        UUID targetKey,
        FeeType feeType,
        FeeRate rate,
        FixedAmount fixedAmount
) {
    public static CreateFeePolicyCommand from(CreateFeePolicyRequestDto createFeePolicyRequestDto) {
        return new CreateFeePolicyCommand(
                TargetType.of(createFeePolicyRequestDto.getTargetType()),
                createFeePolicyRequestDto.getTargetKey(),
                FeeType.of(createFeePolicyRequestDto.getFeeType()),
                FeeRate.of(createFeePolicyRequestDto.getRate()),
                FixedAmount.of(createFeePolicyRequestDto.getFixedAmount())
        );
    }
}

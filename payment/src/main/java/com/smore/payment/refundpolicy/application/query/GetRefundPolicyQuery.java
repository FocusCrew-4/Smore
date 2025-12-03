package com.smore.payment.refundpolicy.application.query;

import com.smore.payment.refundpolicy.domain.model.RefundTargetType;
import com.smore.payment.refundpolicy.presentation.dto.request.GetRefundPolicyRequestDto;

import java.util.UUID;

public record GetRefundPolicyQuery(
        RefundTargetType refundTargetType,
        UUID targetKey
) {
    public static GetRefundPolicyQuery from(GetRefundPolicyRequestDto getRefundPolicyRequestDto) {
        return new GetRefundPolicyQuery(
                RefundTargetType.of(getRefundPolicyRequestDto.getRefundTargetType()),
                getRefundPolicyRequestDto.getTargetKey()
        );
    }
}

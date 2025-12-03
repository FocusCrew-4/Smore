package com.smore.payment.refundpolicy.application.command;

import com.smore.payment.refundpolicy.domain.model.RefundFeeRate;
import com.smore.payment.refundpolicy.domain.model.RefundFeeType;
import com.smore.payment.refundpolicy.domain.model.RefundFixedAmount;
import com.smore.payment.refundpolicy.domain.model.RefundTargetType;
import com.smore.payment.refundpolicy.presentation.dto.request.CreateRefundPolicyRequestDto;

import java.util.UUID;

public record CreateRefundPolicyCommand(
        RefundTargetType refundTargetType,
        UUID targetKey,
        Integer refundPeriodDays,
        RefundFeeType refundFeeType,
        RefundFeeRate rate,
        RefundFixedAmount refundFixedAmount,
        boolean refundable
) {
    public static CreateRefundPolicyCommand from(CreateRefundPolicyRequestDto createRefundPolicyRequestDto) {
        return new CreateRefundPolicyCommand(
                RefundTargetType.of(createRefundPolicyRequestDto.getRefundTargetType()),
                createRefundPolicyRequestDto.getTargetKey(),
                createRefundPolicyRequestDto.getRefundPeriodDays(),
                RefundFeeType.of(createRefundPolicyRequestDto.getRefundFeeType()),
                RefundFeeRate.of(createRefundPolicyRequestDto.getRate()),
                RefundFixedAmount.of(createRefundPolicyRequestDto.getRefundFixedAmount()),
                createRefundPolicyRequestDto.isRefundable()
        );
    }
}

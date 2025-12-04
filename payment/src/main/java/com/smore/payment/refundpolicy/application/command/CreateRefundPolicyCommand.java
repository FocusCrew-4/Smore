package com.smore.payment.refundpolicy.application.command;

import com.smore.payment.refundpolicy.domain.model.*;
import com.smore.payment.refundpolicy.presentation.dto.request.CreateRefundPolicyRequestDto;

import java.time.Duration;
import java.util.UUID;

public record CreateRefundPolicyCommand(
        RefundTargetType refundTargetType,
        TargetKey targetKey,
        Duration refundPeriodDays,
        RefundFeeType refundFeeType,
        RefundFeeRate rate,
        RefundFixedAmount refundFixedAmount,
        boolean refundable
) {}

package com.smore.payment.policy.refund.application.command;

import com.smore.payment.policy.refund.domain.model.*;

import java.time.Duration;

public record CreateRefundPolicyCommand(
        RefundTargetType refundTargetType,
        TargetKey targetKey,
        Duration refundPeriodDays,
        RefundFeeType refundFeeType,
        RefundFeeRate rate,
        RefundFixedAmount refundFixedAmount,
        boolean refundable
) {}

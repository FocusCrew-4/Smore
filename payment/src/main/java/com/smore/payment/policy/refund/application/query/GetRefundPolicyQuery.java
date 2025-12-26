package com.smore.payment.policy.refund.application.query;

import com.smore.payment.policy.refund.domain.model.RefundTargetType;
import com.smore.payment.policy.refund.domain.model.TargetKey;

public record GetRefundPolicyQuery(
        RefundTargetType refundTargetType,
        TargetKey targetKey
) {}

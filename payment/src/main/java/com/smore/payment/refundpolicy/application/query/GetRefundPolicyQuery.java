package com.smore.payment.refundpolicy.application.query;

import com.smore.payment.refundpolicy.domain.model.RefundTargetType;
import com.smore.payment.refundpolicy.domain.model.TargetKey;

public record GetRefundPolicyQuery(
        RefundTargetType refundTargetType,
        TargetKey targetKey
) {}

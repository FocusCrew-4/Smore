package com.smore.payment.policy.fee.application.query;

import com.smore.payment.policy.fee.domain.model.TargetKey;
import com.smore.payment.policy.fee.domain.model.TargetType;

public record GetFeePolicyQuery(
        TargetType targetType,
        TargetKey targetKey
) {

}

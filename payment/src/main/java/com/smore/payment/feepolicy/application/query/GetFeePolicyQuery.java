package com.smore.payment.feepolicy.application.query;

import com.smore.payment.feepolicy.domain.model.TargetKey;
import com.smore.payment.feepolicy.domain.model.TargetType;

public record GetFeePolicyQuery(
        TargetType targetType,
        TargetKey targetKey
) {

}

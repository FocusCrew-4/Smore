package com.smore.payment.cancelpolicy.application.query;

import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import com.smore.payment.cancelpolicy.domain.model.TargetKey;

public record GetCancelPolicyQuery(
        CancelTargetType cancelTargetType,
        TargetKey targetKey
) {

}

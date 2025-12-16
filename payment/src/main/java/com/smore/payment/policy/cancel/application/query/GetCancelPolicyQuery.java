package com.smore.payment.policy.cancel.application.query;

import com.smore.payment.policy.cancel.domain.model.CancelTargetType;
import com.smore.payment.policy.cancel.domain.model.TargetKey;

public record GetCancelPolicyQuery(
        CancelTargetType cancelTargetType,
        TargetKey targetKey
) {

}

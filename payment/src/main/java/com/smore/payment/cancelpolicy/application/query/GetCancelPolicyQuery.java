package com.smore.payment.cancelpolicy.application.query;

import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;

import java.util.UUID;

public record GetCancelPolicyQuery(
        CancelTargetType cancelTargetType,
        UUID targetKey
) {
}

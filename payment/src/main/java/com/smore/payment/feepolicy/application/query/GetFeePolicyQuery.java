package com.smore.payment.feepolicy.application.query;

import com.smore.payment.feepolicy.domain.model.TargetType;

import java.util.UUID;

public record GetFeePolicyQuery(
        TargetType targetType,
        UUID TargetKey
) {}

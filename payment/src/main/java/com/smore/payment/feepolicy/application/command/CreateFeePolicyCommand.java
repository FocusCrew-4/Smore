package com.smore.payment.feepolicy.application.command;

import com.smore.payment.feepolicy.domain.model.FeeType;
import com.smore.payment.feepolicy.domain.model.FeeRate;
import com.smore.payment.feepolicy.domain.model.FixedAmount;
import com.smore.payment.feepolicy.domain.model.TargetType;

import java.util.UUID;

public record CreateFeePolicyCommand(
        TargetType targetType,
        UUID targetKey,
        FeeType feeType,
        FeeRate rate,
        FixedAmount fixedAmount
) {}

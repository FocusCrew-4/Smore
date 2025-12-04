package com.smore.payment.feepolicy.application.command;

import com.smore.payment.feepolicy.domain.model.*;

public record CreateFeePolicyCommand(
        TargetType targetType,
        TargetKey targetKey,
        FeeType feeType,
        FeeRate rate,
        FixedAmount fixedAmount
) {
}

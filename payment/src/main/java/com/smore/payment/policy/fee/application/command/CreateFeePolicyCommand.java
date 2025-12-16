package com.smore.payment.policy.fee.application.command;

import com.smore.payment.policy.fee.domain.model.*;

public record CreateFeePolicyCommand(
        TargetType targetType,
        TargetKey targetKey,
        FeeType feeType,
        FeeRate rate,
        FixedAmount fixedAmount
) {
}

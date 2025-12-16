package com.smore.payment.policy.cancel.application.command;

import com.smore.payment.policy.cancel.domain.model.*;

import java.time.Duration;

public record CreateCancelPolicyCommand(
        CancelTargetType cancelTargetType,
        TargetKey targetKey,
        Duration cancelLimitMinutes,
        CancelFeeType cancelFeeType,
        CancelFeeRate rate,
        CancelFixedAmount cancelFixedAmount,
        boolean cancellable
) {
}

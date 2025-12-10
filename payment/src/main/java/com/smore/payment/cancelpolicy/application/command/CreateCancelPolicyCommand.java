package com.smore.payment.cancelpolicy.application.command;

import com.smore.payment.cancelpolicy.domain.model.*;

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

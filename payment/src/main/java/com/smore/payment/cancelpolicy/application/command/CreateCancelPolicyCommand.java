package com.smore.payment.cancelpolicy.application.command;

import com.smore.payment.cancelpolicy.domain.model.CancelFeeRate;
import com.smore.payment.cancelpolicy.domain.model.CancelFeeType;
import com.smore.payment.cancelpolicy.domain.model.CancelFixedAmount;
import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;

import java.util.UUID;

public record CreateCancelPolicyCommand(
        CancelTargetType cancelTargetType,
        UUID targetKey,
        Integer cancelLimitMinutes,
        CancelFeeType cancelFeeType,
        CancelFeeRate rate,
        CancelFixedAmount cancelFixedAmount,
        boolean cancellable
) {
}

package com.smore.payment.policy.refund.presentation.dto.response;

import com.smore.payment.policy.refund.domain.model.RefundPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetRefundPolicyResponseDto {

    private UUID id;
    private String targetType;
    private String targetKey;
    private Duration refundPeriodDays;
    private String feeType;
    private BigDecimal rate;
    private BigDecimal fixedAmount;
    private boolean refundable;
    private boolean active;

    public static GetRefundPolicyResponseDto from(RefundPolicy refundPolicy) {
        return new GetRefundPolicyResponseDto(
                refundPolicy.getId(),
                refundPolicy.getRefundTargetType().toString(),
                refundPolicy.getTargetKey().toString(),
                refundPolicy.getRefundPeriodDays(),
                refundPolicy.getRefundFeeType().toString(),
                refundPolicy.getRefundFeeRate().value(),
                refundPolicy.getRefundFixedAmount().value(),
                refundPolicy.isRefundable(),
                refundPolicy.isActive()
        );
    }
}

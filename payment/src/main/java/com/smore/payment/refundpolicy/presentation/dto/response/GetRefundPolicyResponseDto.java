package com.smore.payment.refundpolicy.presentation.dto.response;

import com.smore.payment.refundpolicy.domain.model.RefundPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetRefundPolicyResponseDto {

    private UUID id;
    private String targetType;
    private UUID targetKey;
    private Integer refundPeriodDays;
    private String feeType;
    private BigDecimal rate;
    private BigDecimal fixedAmount;
    private boolean refundable;
    private boolean active;

    public static GetRefundPolicyResponseDto from(RefundPolicy refundPolicy) {
        return new GetRefundPolicyResponseDto(
                refundPolicy.getId(),
                refundPolicy.getRefundTargetType().toString(),
                refundPolicy.getTargetKey(),
                refundPolicy.getRefundPeriodDays(),
                refundPolicy.getRefundFeeType().toString(),
                refundPolicy.getRefundFeeRate().value(),
                refundPolicy.getRefundFixedAmount().value(),
                refundPolicy.isRefundable(),
                refundPolicy.isActive()
        );
    }
}

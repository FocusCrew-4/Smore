package com.smore.payment.feepolicy.presentation.dto.response;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetFeePolicyResponseDto {
    private UUID id;
    private String targetType;
    private String targetKey;
    private String feeType;
    private BigDecimal rate;
    private BigDecimal fixedAmount;
    private boolean active;

    public static GetFeePolicyResponseDto from(FeePolicy feePolicy) {
        return new GetFeePolicyResponseDto(
                feePolicy.getId(),
                feePolicy.getTargetType().toString(),
                feePolicy.getTargetKey().getValueAsString(),
                feePolicy.getFeeType().toString(),
                feePolicy.getRate().value(),
                feePolicy.getFixedAmount().value(),
                feePolicy.isActive()
        );
    }
}

package com.smore.payment.refundpolicy.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateRefundPolicyRequestDto {

    @NotNull
    private String refundTargetType;

    @NotNull
    private UUID targetKey;

    @NotNull
    private Integer refundPeriodDays;

    @NotNull
    private String refundFeeType;

    private BigDecimal rate;
    private BigDecimal refundFixedAmount;

    @NotNull
    private boolean refundable;

}

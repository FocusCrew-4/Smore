package com.smore.payment.cancelpolicy.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateCancelPolicyRequestDto {

    @NotNull
    private String cancelTargetType;

    @NotNull
    private UUID targetKey;

    @NotNull
    private Integer cancelLimitMinutes;

    @NotNull
    private String cancelFeeType;

    private BigDecimal rate;
    private BigDecimal cancelFixedAmount;

    @NotNull
    private boolean cancellable;

}

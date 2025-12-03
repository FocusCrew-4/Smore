package com.smore.payment.feepolicy.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateFeePolicyRequestDto {

    @NotNull
    private String targetType;

    @NotNull
    private UUID targetKey;

    @NotNull
    private String feeType;

    private BigDecimal rate;
    private BigDecimal fixedAmount;

}

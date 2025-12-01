package com.smore.payment.feepolicy.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateFeePolicyRequestDto {


    private String targetType;


    private UUID targetKey;


    private String feeType;

    private BigDecimal rate;
    private BigDecimal fixedAmount;

}

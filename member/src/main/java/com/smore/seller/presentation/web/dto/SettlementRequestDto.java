package com.smore.seller.presentation.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SettlementRequestDto(
    @NotNull
    @Positive
    BigDecimal reqAmount
) {

}

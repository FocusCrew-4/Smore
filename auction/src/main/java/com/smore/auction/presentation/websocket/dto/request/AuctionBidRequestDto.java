package com.smore.auction.presentation.websocket.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AuctionBidRequestDto(
    @NotNull
    BigDecimal bidPrice,
    @NotNull
    @Max(1)
    @Min(1)
    Integer quantity,
    @NotNull
    UUID idempotencyKey
) {

}

package com.smore.order.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record RefundRequest(

    @NotNull(message = "orderId는 필수값입니다.")
    UUID orderId,

    @Positive(message = "환불 수량은 1개 이상이어야 합니다.")
    @Max(value = 1000, message = "환불 수량은 1000개 이하여야 합니다.")
    Integer refundQuantity,

    @NotBlank(message = "환불 사유는 필수값입니다.")
    String reason,

    @NotNull(message = "idempotencyKey는 필수값입니다.")
    UUID idempotencyKey

) {}
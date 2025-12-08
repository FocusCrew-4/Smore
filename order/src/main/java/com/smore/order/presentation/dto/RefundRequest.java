package com.smore.order.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefundRequest {

    @NotNull(message = "orderId는 필수값입니다.")
    private final UUID orderId;

    @Positive(message = "환불 수량은 1개 이상이어야 합니다.")
    @Max(message = "환불 수량은 1000개 이하여야 합니다.", value = 1000)
    private final Integer refundQuantity;

    @NotBlank(message = "환불 사유는 필수값입니다.")
    private final String reason;

    @NotNull(message = "idempotencyKey는 필수값입니다.")
    private final UUID idempotencyKey;

}

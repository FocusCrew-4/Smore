package com.smore.bidcompetition.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record BidRequest(
    @NotNull(message = "bidId는 필수값입니다.")
    UUID bidId,

    @Positive(message = "quantity는 양수여야 합니다.")
    Integer quantity,

    @NotNull(message = "idempotencyKey는 필수값입니다.")
    UUID idempotencyKey,

    @NotBlank(message = "도로 명 주소는 필수값입니다.")
    String street,

    @NotBlank(message = "지번 주소는 필수값입니다.")
    String city,

    @NotBlank(message = "우편 번호는 필수값입니다.")
    String zipcode
) {

}


package com.smore.seller.presentation.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SellerApplyRequestDto(
    @NotBlank
    String accountNum
) {
}

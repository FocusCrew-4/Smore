package com.smore.order.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ModifyOrderRequest(

    @NotBlank(message = "도로 명 주소는 필수값입니다.")
    String street,

    @NotBlank(message = "지번 주소는 필수값입니다.")
    String city,

    @NotBlank(message = "우편 번호는 필수값입니다.")
    String zipcode
) {}

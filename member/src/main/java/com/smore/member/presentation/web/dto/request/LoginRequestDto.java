package com.smore.member.presentation.web.dto.request;

public record LoginRequestDto(
    String email,
    String password
) {

}

package com.smore.member.presentation.web.dto.request;

public record UpdateInfoRequestDto(
    String nickname,
    String email,
    String password
) {

}

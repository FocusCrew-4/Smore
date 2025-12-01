package com.smore.member.presentation.web.dto.response;

public record CreateResponseDto(
    Long id,
    String email,
    String nickname
) {

}

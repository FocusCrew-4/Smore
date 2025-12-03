package com.smore.member.presentation.web.dto.request;

import com.smore.member.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRequestDto(
    @NotNull
    Role role,
    @NotBlank
    @Email
    String email,
    @NotBlank
    String password,
    @NotBlank
    String nickname
) {
    public CreateRequestDto {
        if (role != Role.SELLER && role != Role.CONSUMER) {
            throw new IllegalArgumentException("SELLER 또는 CONSUMER만 등록 가능합니다.");
        }
    }
}

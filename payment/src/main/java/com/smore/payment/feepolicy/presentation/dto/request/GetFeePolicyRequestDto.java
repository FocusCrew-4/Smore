package com.smore.payment.feepolicy.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetFeePolicyRequestDto {

    @NotNull
    private String targetType;

    @NotNull
    private UUID targetKey;
}

package com.smore.payment.cancelpolicy.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetCancelPolicyRequestDto {

    @NotNull
    private String cancelTargetType;

    @NotNull
    private UUID targetKey;
}

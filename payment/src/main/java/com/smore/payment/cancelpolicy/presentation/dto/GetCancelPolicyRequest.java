package com.smore.payment.cancelpolicy.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetCancelPolicyRequest {

    @NotNull
    private String cancelTargetType;

    @NotNull
    private UUID targetKey;
}

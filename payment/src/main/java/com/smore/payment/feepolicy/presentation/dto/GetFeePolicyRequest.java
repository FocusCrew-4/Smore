package com.smore.payment.feepolicy.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetFeePolicyRequest {

    @NotNull
    private String TargetType;

    @NotNull
    private UUID TargetKey;
}

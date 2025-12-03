package com.smore.payment.refundpolicy.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetRefundPolicyRequestDto {

    @NotNull
    private String refundTargetType;

    @NotNull
    private UUID targetKey;
}

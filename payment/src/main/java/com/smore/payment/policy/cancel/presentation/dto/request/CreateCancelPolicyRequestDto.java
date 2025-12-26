package com.smore.payment.policy.cancel.presentation.dto.request;

import com.smore.payment.policy.cancel.application.command.CreateCancelPolicyCommand;
import com.smore.payment.policy.cancel.domain.model.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateCancelPolicyRequestDto {

    @NotNull
    private String targetType;

    @NotNull
    private String targetKey;

    @NotNull
    private Integer cancelLimitMinutes;

    @NotNull
    private String cancelFeeType;

    private BigDecimal rate;
    private BigDecimal cancelFixedAmount;

    @NotNull
    private boolean cancellable;

    public CreateCancelPolicyCommand toCommand() {

        CancelTargetType cancelTargetType = CancelTargetType.of(targetType);

        TargetKey parsedKey = switch (cancelTargetType) {
            case CATEGORY -> new TargetKeyUUID(UUID.fromString(targetKey));
            case MERCHANT -> new TargetKeyLong(Long.parseLong(targetKey));
            case AUCTION_TYPE -> new TargetKeyString(targetKey);
            case USER_TYPE -> null;
        };

        return new CreateCancelPolicyCommand(
                cancelTargetType,
                parsedKey,
                Duration.ofMinutes(cancelLimitMinutes),
                CancelFeeType.of(cancelFeeType),
                CancelFeeRate.of(rate),
                CancelFixedAmount.of(cancelFixedAmount),
                cancellable
        );
    }
}

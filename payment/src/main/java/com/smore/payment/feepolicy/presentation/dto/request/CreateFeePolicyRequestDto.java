package com.smore.payment.feepolicy.presentation.dto.request;

import com.smore.payment.feepolicy.application.command.CreateFeePolicyCommand;
import com.smore.payment.feepolicy.domain.model.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateFeePolicyRequestDto {

    @NotNull
    private String targetType;

    @NotNull
    private String targetKey;

    @NotNull
    private String feeType;

    private BigDecimal rate;
    private BigDecimal fixedAmount;

    public CreateFeePolicyCommand toCommand() {

        TargetType type = TargetType.of(targetType);

        TargetKey parsedTargetKey = switch (type) {
            case CATEGORY -> new TargetKeyLong(Long.parseLong(targetKey));
            case MERCHANT -> new TargetKeyUUID(UUID.fromString(targetKey));
            case USER_TYPE -> null;
        };

        return new CreateFeePolicyCommand(
                type,
                parsedTargetKey,
                FeeType.of(feeType),
                FeeRate.of(rate),
                FixedAmount.of(fixedAmount)
        );
    }

}

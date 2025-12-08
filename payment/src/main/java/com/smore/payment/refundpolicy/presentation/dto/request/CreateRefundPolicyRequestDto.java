package com.smore.payment.refundpolicy.presentation.dto.request;

import com.smore.payment.refundpolicy.application.command.CreateRefundPolicyCommand;
import com.smore.payment.refundpolicy.domain.model.*;
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
public class CreateRefundPolicyRequestDto {

    @NotNull
    private String targetType;

    @NotNull
    private String targetKey;

    @NotNull
    private Integer refundPeriodDays;

    @NotNull
    private String refundFeeType;

    private BigDecimal rate;
    private BigDecimal refundFixedAmount;

    @NotNull
    private boolean refundable;

    public CreateRefundPolicyCommand toCommand() {

        RefundTargetType refundTargetType = RefundTargetType.of(targetType);

        TargetKey parsedTargetKey = switch (refundTargetType) {
            case CATEGORY -> new TargetKeyUUID(UUID.fromString(targetKey));
            case MERCHANT -> new TargetKeyLong(Long.parseLong(targetKey));
            case AUCTION_TYPE -> new TargetKeyString(targetKey);
            case USER_TYPE -> null;
        };

        return new CreateRefundPolicyCommand(
                refundTargetType,
                parsedTargetKey,
                Duration.ofDays(refundPeriodDays),
                RefundFeeType.of(refundFeeType),
                RefundFeeRate.of(rate),
                RefundFixedAmount.of(refundFixedAmount),
                refundable
        );
    }
}

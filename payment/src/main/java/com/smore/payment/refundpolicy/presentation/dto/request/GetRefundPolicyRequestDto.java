package com.smore.payment.refundpolicy.presentation.dto.request;

import com.smore.payment.refundpolicy.application.query.GetRefundPolicyQuery;
import com.smore.payment.refundpolicy.domain.model.*;
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
    private String targetType;

    @NotNull
    private String targetKey;

    public GetRefundPolicyQuery toQuery() {

        RefundTargetType refundTargetType = RefundTargetType.of(targetType);

        TargetKey parsedTargetKey = switch (refundTargetType) {
            case CATEGORY -> new TargetKeyUUID(UUID.fromString(targetKey));
            case MERCHANT -> new TargetKeyLong(Long.parseLong(targetKey));
            case AUCTION_TYPE -> new TargetKeyString(targetKey);
            case USER_TYPE -> null;
        };

        return new GetRefundPolicyQuery(
                refundTargetType,
                parsedTargetKey
        );
    }
}

package com.smore.payment.cancelpolicy.presentation.dto.request;

import com.smore.payment.cancelpolicy.application.query.GetCancelPolicyQuery;
import com.smore.payment.cancelpolicy.domain.model.*;

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
    private String targetType;

    @NotNull
    private String targetKey;

    public GetCancelPolicyQuery toQuery() {

        CancelTargetType cancelTargetType = CancelTargetType.of(targetType);

        TargetKey parsedTargetKey = switch (cancelTargetType) {
            case CATEGORY -> new TargetKeyUUID(UUID.fromString(targetKey));
            case MERCHANT -> new TargetKeyLong(Long.parseLong(targetKey));
            case AUCTION_TYPE -> new TargetKeyString(targetKey);
            case USER_TYPE -> null;
        };

        return new GetCancelPolicyQuery(
                cancelTargetType,
                parsedTargetKey
        );
    }
}

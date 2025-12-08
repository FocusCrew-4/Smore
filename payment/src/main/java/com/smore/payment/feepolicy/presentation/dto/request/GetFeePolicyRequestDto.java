package com.smore.payment.feepolicy.presentation.dto.request;

import com.smore.payment.feepolicy.application.query.GetFeePolicyQuery;
import com.smore.payment.feepolicy.domain.model.TargetKey;
import com.smore.payment.feepolicy.domain.model.TargetKeyLong;
import com.smore.payment.feepolicy.domain.model.TargetKeyUUID;
import com.smore.payment.feepolicy.domain.model.TargetType;
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
    private String targetKey;

    public GetFeePolicyQuery toQuery() {

        TargetType type = TargetType.of(targetType);

        TargetKey parsedTargetKey = switch (type) {
            case CATEGORY -> new TargetKeyLong(Long.parseLong(targetKey));
            case MERCHANT -> new TargetKeyUUID(UUID.fromString(targetKey));
            case USER_TYPE -> null;
        };

        return new GetFeePolicyQuery(
                type,
                parsedTargetKey
        );
    }
}

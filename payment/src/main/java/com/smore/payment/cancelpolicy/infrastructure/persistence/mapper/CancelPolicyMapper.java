package com.smore.payment.cancelpolicy.infrastructure.persistence.mapper;

import com.smore.payment.cancelpolicy.domain.model.*;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelFeeRateJpa;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelFixedAmountJpa;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelPolicyEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.smore.payment.cancelpolicy.domain.model.CancelTargetType.*;

@Component
public class CancelPolicyMapper {

    public CancelPolicyEntity toEntity(CancelPolicy cancelPolicy) {
        CancelFeeRateJpa cancelFeeRateJpa = new CancelFeeRateJpa(
                cancelPolicy.getCancelFeeRate().value()
        );
        CancelFixedAmountJpa cancelFixedAmountJpa = new CancelFixedAmountJpa(
                cancelPolicy.getCancelFixedAmount().value()
        );

        return new CancelPolicyEntity(
                cancelPolicy.getId(),
                cancelPolicy.getCancelTargetType(),
                cancelPolicy.getTargetKey().getValueAsString(),
                cancelPolicy.getCancelLimitMinutes(),
                cancelPolicy.getCancelFeeType(),
                cancelFeeRateJpa,
                cancelFixedAmountJpa,
                cancelPolicy.isCancellable(),
                cancelPolicy.isActive()
        );
    }

    public CancelPolicy toDomainEntity(CancelPolicyEntity cancelPolicyEntity) {
        CancelFeeRate feeRate = new CancelFeeRate(
                cancelPolicyEntity.getCancelFeeRateJpa().getRate()
        );
        CancelFixedAmount fixedAmount = new CancelFixedAmount(
                cancelPolicyEntity.getCancelFixedAmountJpa().getFixedAmount()
        );
        TargetKey targetKey = switch (cancelPolicyEntity.getCancelTargetType()) {
            case CATEGORY -> new TargetKeyUUID(UUID.fromString(cancelPolicyEntity.getTargetKey()));
            case MERCHANT -> new TargetKeyLong(Long.parseLong(cancelPolicyEntity.getTargetKey()));
            case AUCTION_TYPE -> new TargetKeyString(cancelPolicyEntity.getTargetKey());
            case USER_TYPE -> null;
        };
        return CancelPolicy.reconstruct(
                cancelPolicyEntity.getId(),
                cancelPolicyEntity.getCancelTargetType(),
                targetKey,
                cancelPolicyEntity.getCancelLimitMinutes(),
                cancelPolicyEntity.getCancelFeeType(),
                feeRate,
                fixedAmount,
                cancelPolicyEntity.isCancellable(),
                cancelPolicyEntity.isActive()
        );
    }
}

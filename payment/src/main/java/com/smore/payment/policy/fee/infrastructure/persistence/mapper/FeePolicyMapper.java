package com.smore.payment.policy.fee.infrastructure.persistence.mapper;

import com.smore.payment.policy.fee.infrastructure.persistence.model.FeePolicyEntity;
import com.smore.payment.policy.fee.infrastructure.persistence.model.FeeRateJpa;
import com.smore.payment.policy.fee.infrastructure.persistence.model.FixedAmountJpa;
import com.smore.payment.policy.fee.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FeePolicyMapper {

    public FeePolicyEntity toEntity(FeePolicy feePolicy) {
        FeeRateJpa feeRateJpa = new FeeRateJpa(
                feePolicy.getRate().value()
        );
        FixedAmountJpa fixedAmountJpa = new FixedAmountJpa(
                feePolicy.getFixedAmount().value()
        );

        return new FeePolicyEntity(
                feePolicy.getId(),
                feePolicy.getTargetType(),
                feePolicy.getTargetKey().getValueAsString(),
                feePolicy.getFeeType(),
                feeRateJpa,
                fixedAmountJpa,
                feePolicy.isActive()
        );
    }

    public FeePolicy toDomainEntity(FeePolicyEntity feePolicyEntity) {
        FeeRate feeRate = new FeeRate(
                feePolicyEntity.getRate().getRate()
        );
        FixedAmount fixedAmount = new FixedAmount(
                feePolicyEntity.getFixedAmount().getFixedAmount()
        );
        TargetKey targetKey = switch (feePolicyEntity.getTargetType()) {
            case MERCHANT -> new TargetKeyLong(Long.parseLong(feePolicyEntity.getTargetKey()));
            case CATEGORY -> new TargetKeyUUID(UUID.fromString(feePolicyEntity.getTargetKey()));
            case USER_TYPE -> null;
        };
        return FeePolicy.reconstruct(
                feePolicyEntity.getId(),
                feePolicyEntity.getTargetType(),
                targetKey,
                feePolicyEntity.getFeeType(),
                feeRate,
                fixedAmount,
                feePolicyEntity.isActive()
        );
    }
}

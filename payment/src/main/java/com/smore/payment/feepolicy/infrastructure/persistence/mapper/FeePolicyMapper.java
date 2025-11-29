package com.smore.payment.feepolicy.infrastructure.persistence.mapper;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.domain.model.FeeRate;
import com.smore.payment.feepolicy.domain.model.FixedAmount;
import com.smore.payment.feepolicy.infrastructure.persistence.model.FeePolicyEntity;
import com.smore.payment.feepolicy.infrastructure.persistence.model.FeeRateJpa;
import com.smore.payment.feepolicy.infrastructure.persistence.model.FixedAmountJpa;
import org.springframework.stereotype.Component;

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

        return FeePolicy.reconstruct(
                feePolicyEntity.getId(),
                feePolicyEntity.getTargetType(),
                feePolicyEntity.getTargetKey(),
                feePolicyEntity.getFeeType(),
                feeRate,
                fixedAmount,
                false,
                feePolicyEntity.getCreatedAt(),
                feePolicyEntity.getUpdatedAt(),
                feePolicyEntity.getDeletedAt(),
                feePolicyEntity.getCreateBy(),
                feePolicyEntity.getUpdatedBy(),
                feePolicyEntity.getDeletedBy()
        );
    }
}

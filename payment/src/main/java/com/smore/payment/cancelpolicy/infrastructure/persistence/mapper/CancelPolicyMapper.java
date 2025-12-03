package com.smore.payment.cancelpolicy.infrastructure.persistence.mapper;

import com.smore.payment.cancelpolicy.domain.model.CancelFeeRate;
import com.smore.payment.cancelpolicy.domain.model.CancelFixedAmount;
import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelFeeRateJpa;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelFixedAmountJpa;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelPolicyEntity;
import org.springframework.stereotype.Component;

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
                cancelPolicy.getTargetKey(),
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

        return CancelPolicy.reconstruct(
                cancelPolicyEntity.getId(),
                cancelPolicyEntity.getCancelTargetType(),
                cancelPolicyEntity.getTargetKey(),
                cancelPolicyEntity.getCancelLimitMinutes(),
                cancelPolicyEntity.getCancelFeeType(),
                feeRate,
                fixedAmount,
                cancelPolicyEntity.isCancellable(),
                cancelPolicyEntity.isActive()
        );
    }
}

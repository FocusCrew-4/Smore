package com.smore.payment.refundpolicy.infrastructure.persistence.mapper;

import com.smore.payment.refundpolicy.domain.model.RefundFeeRate;
import com.smore.payment.refundpolicy.domain.model.RefundFixedAmount;
import com.smore.payment.refundpolicy.domain.model.RefundPolicy;
import com.smore.payment.refundpolicy.infrastructure.persistence.model.RefundFeeRateJpa;
import com.smore.payment.refundpolicy.infrastructure.persistence.model.RefundFixedAmountJpa;
import com.smore.payment.refundpolicy.infrastructure.persistence.model.RefundPolicyEntity;
import org.springframework.stereotype.Component;

@Component
public class RefundPolicyMapper {

    public RefundPolicyEntity toEntity(RefundPolicy refundPolicy) {
        RefundFeeRateJpa refundFeeRateJpa = new RefundFeeRateJpa(
                refundPolicy.getRefundFeeRate().value()
        );
        RefundFixedAmountJpa refundFixedAmountJpa = new RefundFixedAmountJpa(
                refundPolicy.getRefundFixedAmount().value()
        );

        return new RefundPolicyEntity(
                refundPolicy.getId(),
                refundPolicy.getRefundTargetType(),
                refundPolicy.getTargetKey(),
                refundPolicy.getRefundPeriodDays(),
                refundPolicy.getRefundFeeType(),
                refundFeeRateJpa,
                refundFixedAmountJpa,
                refundPolicy.isRefundable(),
                refundPolicy.isActive()
        );
    }

    public RefundPolicy toDomainEntity(RefundPolicyEntity refundPolicyEntity) {
        RefundFeeRate feeRate = RefundFeeRate.of(
                refundPolicyEntity.getRefundFeeRateJpa().getRate()
        );
        RefundFixedAmount fixedAmount = RefundFixedAmount.of(
                refundPolicyEntity.getRefundFixedAmountJpa().getFixedAmount()
        );

        return RefundPolicy.reconstruct(
                refundPolicyEntity.getId(),
                refundPolicyEntity.getRefundTargetType(),
                refundPolicyEntity.getTargetKey(),
                refundPolicyEntity.getRefundPeriodDays(),
                refundPolicyEntity.getRefundFeeType(),
                feeRate,
                fixedAmount,
                refundPolicyEntity.isRefundable(),
                refundPolicyEntity.isActive()
        );
    }
}

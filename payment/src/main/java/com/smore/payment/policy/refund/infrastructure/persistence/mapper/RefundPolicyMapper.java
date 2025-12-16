package com.smore.payment.policy.refund.infrastructure.persistence.mapper;

import com.smore.payment.policy.refund.domain.model.*;
import com.smore.payment.policy.refund.infrastructure.persistence.model.RefundFeeRateJpa;
import com.smore.payment.policy.refund.infrastructure.persistence.model.RefundFixedAmountJpa;
import com.smore.payment.policy.refund.infrastructure.persistence.model.RefundPolicyEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                refundPolicy.getTargetKey().getValueAsString(),
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
        TargetKey targetKey = switch (refundPolicyEntity.getRefundTargetType()) {
            case CATEGORY -> new TargetKeyUUID(UUID.fromString(refundPolicyEntity.getTargetKey()));
            case MERCHANT -> new TargetKeyLong(Long.parseLong(refundPolicyEntity.getTargetKey()));
            case AUCTION_TYPE -> new TargetKeyString(refundPolicyEntity.getTargetKey());
            case USER_TYPE -> null;
        };

        return RefundPolicy.reconstruct(
                refundPolicyEntity.getId(),
                refundPolicyEntity.getRefundTargetType(),
                targetKey,
                refundPolicyEntity.getRefundPeriodDays(),
                refundPolicyEntity.getRefundFeeType(),
                feeRate,
                fixedAmount,
                refundPolicyEntity.isRefundable(),
                refundPolicyEntity.isActive()
        );
    }
}

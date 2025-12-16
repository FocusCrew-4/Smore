package com.smore.payment.payment.application.facade;

import com.smore.payment.payment.application.facade.dto.RefundPolicyResult;
import com.smore.payment.policy.refund.domain.model.RefundPolicy;
import com.smore.payment.policy.refund.domain.repository.RefundPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefundPolicyFacade {

    private final RefundPolicyRepository refundPolicyRepository;

    public RefundPolicyResult findApplicablePolicy(Long sellerId, UUID categoryId, String auctionType) {

        RefundPolicy refundPolicy = refundPolicyRepository.findApplicablePolicy(
                sellerId,
                categoryId,
                auctionType
        ).orElseThrow(() -> new IllegalStateException("적용 가능한 환불 정책이 존재하지 않습니다."));

        return new RefundPolicyResult(
                refundPolicy.getRefundPeriodDays(),
                refundPolicy.getRefundFeeType().toString(),
                refundPolicy.getRefundFeeRate() != null ? refundPolicy.getRefundFeeRate().value() : BigDecimal.ZERO,
                refundPolicy.getRefundFixedAmount() != null ? refundPolicy.getRefundFixedAmount().value() : BigDecimal.ZERO,
                refundPolicy.isRefundable()
        );

    }
}

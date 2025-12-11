package com.smore.payment.payment.application.facade;

import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.domain.repository.CancelPolicyRepository;
import com.smore.payment.payment.application.facade.dto.CancelPolicyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CancelPolicyFacade {

    private final CancelPolicyRepository cancelPolicyRepository;

    public CancelPolicyResult findApplicablePolicy(Long sellerId, UUID categoryId, String auctionType) {

        CancelPolicy cancelPolicy = cancelPolicyRepository.findApplicablePolicy(
                sellerId,
                categoryId,
                auctionType
        ).orElseThrow(() -> new IllegalStateException("적용 가능한 취소 정책이 존재하지 않습니다."));

        return new CancelPolicyResult(
                cancelPolicy.getCancelLimitMinutes(),
                cancelPolicy.getCancelFeeType().toString(),
                cancelPolicy.getCancelFeeRate() != null ? cancelPolicy.getCancelFeeRate().value() : BigDecimal.ZERO,
                cancelPolicy.getCancelFixedAmount() != null ? cancelPolicy.getCancelFixedAmount().value() : BigDecimal.ZERO,
                cancelPolicy.isCancellable()
        );
    }
}

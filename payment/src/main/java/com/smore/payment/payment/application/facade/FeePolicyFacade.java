package com.smore.payment.payment.application.facade;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.domain.repository.FeePolicyRepository;
import com.smore.payment.payment.application.facade.dto.FeePolicyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeePolicyFacade {

    private final FeePolicyRepository feePolicyRepository;

    public FeePolicyResult findApplicablePolicy(
            Long sellerId,
            UUID categoryId
    ) {
        FeePolicy feePolicy = feePolicyRepository.findApplicablePolicy(
                sellerId,
                categoryId
        ).orElseThrow(() -> new IllegalStateException("적용 가능한 수수료 정책이 존재하지 않습니다."));

        // 2. Settlement 계산에 필요한 값만 Facade DTO로 변환
        return new FeePolicyResult(
                feePolicy.getFeeType().name(),
                feePolicy.getRate() != null ? feePolicy.getRate().value() : BigDecimal.ZERO,
                feePolicy.getFixedAmount() != null ? feePolicy.getFixedAmount().value() : BigDecimal.ZERO
        );
    }
}

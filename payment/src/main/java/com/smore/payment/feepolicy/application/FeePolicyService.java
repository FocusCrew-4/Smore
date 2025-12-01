package com.smore.payment.feepolicy.application;

import com.smore.payment.feepolicy.application.command.CreateFeePolicyCommand;
import com.smore.payment.feepolicy.application.query.GetFeePolicyQuery;
import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.domain.repository.FeePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FeePolicyService {

    private final FeePolicyRepository feePolicyRepository;

    public UUID createFeePolicy(CreateFeePolicyCommand createFeePolicyCommand) {

        FeePolicy feePolicy = FeePolicy.create(
                createFeePolicyCommand.targetType(),
                createFeePolicyCommand.targetKey(),
                createFeePolicyCommand.feeType(),
                createFeePolicyCommand.rate(),
                createFeePolicyCommand.fixedAmount()
        );

        feePolicyRepository.save(feePolicy);

        return feePolicy.getId();
    }

    @Transactional(readOnly = true)
    public FeePolicy getFeePolicy(GetFeePolicyQuery getFeePolicyQuery) {
        return feePolicyRepository.findByTargetTypeAndTargetKey(
                getFeePolicyQuery.targetType(),
                getFeePolicyQuery.TargetKey()
        ).orElseThrow(() -> new IllegalArgumentException("판매자 또는 카테고리에 해당하는 수수료 정책을 찾을 수 없습니다."));
    }

    public void deleteFeePolicy(UUID id, UUID userId) {
        FeePolicy feePolicy = findFeePolicyById(id);
        feePolicy.deactivate();
        feePolicyRepository.delete(feePolicy, userId);
    }

    @Transactional(readOnly = true)
    public FeePolicy findFeePolicyById(UUID id) {
        return feePolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 수수료 정책을 찾을 수 없습니다."));
    }

}

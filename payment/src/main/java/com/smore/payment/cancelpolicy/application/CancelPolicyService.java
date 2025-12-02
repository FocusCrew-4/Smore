package com.smore.payment.cancelpolicy.application;

import com.smore.payment.cancelpolicy.application.command.CreateCancelPolicyCommand;
import com.smore.payment.cancelpolicy.application.query.GetCancelPolicyQuery;
import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.domain.repository.CancelPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelPolicyService {

    private final CancelPolicyRepository cancelPolicyRepository;

    public UUID createCancelPolicy(CreateCancelPolicyCommand createCancelPolicyCommand) {

        CancelPolicy cancelPolicy = CancelPolicy.create(
                createCancelPolicyCommand.cancelTargetType(),
                createCancelPolicyCommand.targetKey(),
                createCancelPolicyCommand.cancelLimitMinutes(),
                createCancelPolicyCommand.cancelFeeType(),
                createCancelPolicyCommand.rate(),
                createCancelPolicyCommand.cancelFixedAmount(),
                createCancelPolicyCommand.cancellable()
        );

        cancelPolicyRepository.save(cancelPolicy);

        return cancelPolicy.getId();
    }

    @Transactional(readOnly = true)
    public CancelPolicy getCancelPolicy(GetCancelPolicyQuery getCancelPolicyQuery) {
        return cancelPolicyRepository.findByTargetTypeAndTargetKey(
                getCancelPolicyQuery.cancelTargetType(),
                getCancelPolicyQuery.targetKey()
        ).orElseThrow(() -> new IllegalArgumentException("판매자 또는 카테고리에 해당하는 수수료 정책을 찾을 수 없습니다."));
    }

    public void deleteCancelPolicy(UUID id, UUID userId) {
        CancelPolicy cancelPolicy = findFeePolicyById(id);
        cancelPolicy.deactivate();
        cancelPolicyRepository.delete(cancelPolicy, userId);
    }

    @Transactional(readOnly = true)
    public CancelPolicy findFeePolicyById(UUID id) {
        return cancelPolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 수수료 정책을 찾을 수 없습니다."));
    }

}

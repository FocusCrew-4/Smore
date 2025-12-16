package com.smore.payment.policy.refund.application;

import com.smore.payment.policy.refund.application.command.CreateRefundPolicyCommand;
import com.smore.payment.policy.refund.application.query.GetRefundPolicyQuery;
import com.smore.payment.policy.refund.domain.model.RefundPolicy;
import com.smore.payment.policy.refund.domain.repository.RefundPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundPolicyService {

    private final RefundPolicyRepository refundPolicyRepository;

    public UUID createRefundPolicy(CreateRefundPolicyCommand createRefundPolicyCommand) {

        RefundPolicy refundPolicy = RefundPolicy.create(
                createRefundPolicyCommand.refundTargetType(),
                createRefundPolicyCommand.targetKey(),
                createRefundPolicyCommand.refundPeriodDays(),
                createRefundPolicyCommand.refundFeeType(),
                createRefundPolicyCommand.rate(),
                createRefundPolicyCommand.refundFixedAmount(),
                createRefundPolicyCommand.refundable()
        );

        refundPolicyRepository.save(refundPolicy);

        return refundPolicy.getId();
    }

    @Transactional(readOnly = true)
    public RefundPolicy getRefundPolicy(GetRefundPolicyQuery getRefundPolicyQuery) {
        return refundPolicyRepository.findByTargetTypeAndTargetKey(
                getRefundPolicyQuery.refundTargetType(),
                getRefundPolicyQuery.targetKey()
        ).orElseThrow(() -> new IllegalArgumentException("판매자 또는 카테고리에 해당하는 수수료 정책을 찾을 수 없습니다."));
    }

    public void deleteRefundPolicy(UUID id, Long userId) {
        RefundPolicy refundPolicy = findRefundPolicyById(id);
        refundPolicy.deactivate();
        refundPolicyRepository.delete(refundPolicy, userId);
    }

    @Transactional(readOnly = true)
    public RefundPolicy findRefundPolicyById(UUID id) {
        return refundPolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 수수료 정책을 찾을 수 없습니다."));
    }

}

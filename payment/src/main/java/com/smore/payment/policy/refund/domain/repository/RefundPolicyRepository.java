package com.smore.payment.policy.refund.domain.repository;

import com.smore.payment.policy.refund.domain.model.RefundPolicy;
import com.smore.payment.policy.refund.domain.model.RefundTargetType;
import com.smore.payment.policy.refund.domain.model.TargetKey;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefundPolicyRepository {
    void save(RefundPolicy refundPolicy);

    void delete(RefundPolicy refundPolicy, Long userId);

    Optional<RefundPolicy> findByTargetTypeAndTargetKey(RefundTargetType refundTargetType, TargetKey targetKey);

    Optional<RefundPolicy> findById(UUID id);

    Optional<RefundPolicy> findApplicablePolicy(Long sellerId, UUID categoryId, String auctionType);
}

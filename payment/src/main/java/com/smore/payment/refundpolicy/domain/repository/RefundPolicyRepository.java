package com.smore.payment.refundpolicy.domain.repository;

import com.smore.payment.refundpolicy.domain.model.RefundPolicy;
import com.smore.payment.refundpolicy.domain.model.RefundTargetType;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefundPolicyRepository {
    void save(RefundPolicy refundPolicy);

    Optional<RefundPolicy> findByTargetTypeAndTargetKey(RefundTargetType refundTargetType, UUID targetKey);

    void delete(RefundPolicy refundPolicy, UUID userId);

    Optional<RefundPolicy> findById(UUID id);
}

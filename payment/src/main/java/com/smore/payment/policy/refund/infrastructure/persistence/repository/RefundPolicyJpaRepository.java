package com.smore.payment.policy.refund.infrastructure.persistence.repository;

import com.smore.payment.policy.refund.domain.model.RefundTargetType;
import com.smore.payment.policy.refund.infrastructure.persistence.model.RefundPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefundPolicyJpaRepository extends JpaRepository<RefundPolicyEntity, UUID> {
    Optional<RefundPolicyEntity> findByRefundTargetTypeAndTargetKey(RefundTargetType refundTargetType, String targetKey);
}

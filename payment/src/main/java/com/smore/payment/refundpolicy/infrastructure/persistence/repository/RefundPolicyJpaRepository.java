package com.smore.payment.refundpolicy.infrastructure.persistence.repository;

import com.smore.payment.refundpolicy.domain.model.RefundTargetType;
import com.smore.payment.refundpolicy.infrastructure.persistence.model.RefundPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefundPolicyJpaRepository extends JpaRepository<RefundPolicyEntity, UUID> {
    Optional<RefundPolicyEntity> findByRefundTargetTypeAndTargetKey(RefundTargetType refundTargetType, UUID targetKey);
}

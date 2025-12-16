package com.smore.payment.policy.cancel.infrastructure.persistence.repository;

import com.smore.payment.policy.cancel.domain.model.CancelTargetType;
import com.smore.payment.policy.cancel.infrastructure.persistence.model.CancelPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CancelPolicyJpaRepository extends JpaRepository<CancelPolicyEntity, UUID> {
    Optional<CancelPolicyEntity> findByCancelTargetTypeAndTargetKey(CancelTargetType cancelTargetType, String targetKey);

}

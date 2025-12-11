package com.smore.payment.cancelpolicy.infrastructure.persistence.repository;

import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CancelPolicyJpaRepository extends JpaRepository<CancelPolicyEntity, UUID> {
    Optional<CancelPolicyEntity> findByCancelTargetTypeAndTargetKey(CancelTargetType cancelTargetType, String targetKey);

}

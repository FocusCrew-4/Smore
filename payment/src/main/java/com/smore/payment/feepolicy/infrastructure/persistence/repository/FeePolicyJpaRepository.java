package com.smore.payment.feepolicy.infrastructure.persistence.repository;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.domain.model.TargetType;
import com.smore.payment.feepolicy.infrastructure.persistence.model.FeePolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeePolicyJpaRepository extends JpaRepository<FeePolicyEntity, UUID> {
    Optional<FeePolicyEntity> findByTargetTypeAndTargetKey(TargetType targetType, UUID targetKey);
}

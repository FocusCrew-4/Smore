package com.smore.payment.feepolicy.domain.repository;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.domain.model.TargetType;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeePolicyRepository {
    void save(FeePolicy feePolicy);

    Optional<FeePolicy> findById(UUID id);

    void delete(FeePolicy feePolicy, UUID userId);

    Optional<FeePolicy> findByTargetTypeAndTargetKey(TargetType targetType, UUID uuid);
}

package com.smore.payment.policy.fee.domain.repository;

import com.smore.payment.policy.fee.domain.model.FeePolicy;
import com.smore.payment.policy.fee.domain.model.TargetKey;
import com.smore.payment.policy.fee.domain.model.TargetType;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeePolicyRepository {
    void save(FeePolicy feePolicy);

    Optional<FeePolicy> findById(UUID id);

    void delete(FeePolicy feePolicy, Long userId);

    Optional<FeePolicy> findByTargetTypeAndTargetKey(TargetType targetType, TargetKey targetKey);

    Optional<FeePolicy> findApplicablePolicy(Long sellerId, UUID categoryId);
}

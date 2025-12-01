package com.smore.payment.feepolicy.domain.repository;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.domain.model.TargetType;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.UUID;

@Repository
public interface FeePolicyRepository {
    void save(FeePolicy feePolicy);

    FeePolicy findById(UUID id);

    void delete(FeePolicy feePolicy);

    FeePolicy findByTargetTypeAndTargetKey(TargetType targetType, UUID uuid);
}

package com.smore.payment.cancelpolicy.domain.repository;

import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CancelPolicyRepository {
    void save(CancelPolicy cancelPolicy);

    Optional<CancelPolicy> findByTargetTypeAndTargetKey(CancelTargetType cancelTargetType, UUID uuid);

    void delete(CancelPolicy cancelPolicy, UUID userId);

    Optional<CancelPolicy> findById(UUID id);
}

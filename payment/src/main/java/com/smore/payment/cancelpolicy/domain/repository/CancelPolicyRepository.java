package com.smore.payment.cancelpolicy.domain.repository;

import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import com.smore.payment.cancelpolicy.domain.model.TargetKey;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CancelPolicyRepository {
    void save(CancelPolicy cancelPolicy);

    Optional<CancelPolicy> findByTargetTypeAndTargetKey(CancelTargetType cancelTargetType, TargetKey targetKey);

    void delete(CancelPolicy cancelPolicy, Long userId);

    Optional<CancelPolicy> findById(UUID id);
}

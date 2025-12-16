package com.smore.payment.policy.cancel.domain.repository;

import com.smore.payment.policy.cancel.domain.model.CancelPolicy;
import com.smore.payment.policy.cancel.domain.model.CancelTargetType;
import com.smore.payment.policy.cancel.domain.model.TargetKey;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CancelPolicyRepository {
    void save(CancelPolicy cancelPolicy);

    Optional<CancelPolicy> findByTargetTypeAndTargetKey(CancelTargetType cancelTargetType, TargetKey targetKey);

    void delete(CancelPolicy cancelPolicy, Long userId);

    Optional<CancelPolicy> findById(UUID id);

    Optional<CancelPolicy> findApplicablePolicy(Long sellerId, UUID categoryId, String auctionType);
}

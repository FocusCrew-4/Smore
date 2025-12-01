package com.smore.payment.feepolicy.domain.repository;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeePolicyRepository {
    void save(FeePolicy feePolicy);

    FeePolicy findById(UUID id);

    void delete(FeePolicy feePolicy);
}

package com.smore.payment.feepolicy.domain.repository;

import com.smore.payment.feepolicy.domain.model.FeePolicy;
import org.springframework.stereotype.Repository;

@Repository
public interface FeePolicyRepository {
    void save(FeePolicy feePolicy);
}

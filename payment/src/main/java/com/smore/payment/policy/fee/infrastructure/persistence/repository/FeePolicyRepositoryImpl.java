package com.smore.payment.policy.fee.infrastructure.persistence.repository;

import com.smore.payment.policy.fee.domain.model.FeePolicy;
import com.smore.payment.policy.fee.domain.model.TargetKey;
import com.smore.payment.policy.fee.domain.model.TargetType;
import com.smore.payment.policy.fee.domain.repository.FeePolicyRepository;
import com.smore.payment.policy.fee.infrastructure.persistence.mapper.FeePolicyMapper;
import com.smore.payment.policy.fee.infrastructure.persistence.model.FeePolicyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FeePolicyRepositoryImpl implements FeePolicyRepository {

    private final FeePolicyJpaRepository feePolicyJpaRepository;
    private final FeePolicyMapper feePolicyMapper;


    @Override
    public void save(FeePolicy feePolicy) {
        feePolicyJpaRepository.save(feePolicyMapper.toEntity(feePolicy));
    }

    @Override
    public Optional<FeePolicy> findById(UUID id) {

        return feePolicyJpaRepository.findById(id)
                .map(feePolicyMapper::toDomainEntity);
    }

    @Override
    public void delete(FeePolicy feePolicy, Long userId) {

        FeePolicyEntity feePolicyEntity = feePolicyJpaRepository.findById(feePolicy.getId())
                .orElseThrow(() -> new IllegalArgumentException("수수료 정책를 찾을 수 없습니다."));

        feePolicyEntity.setActive(feePolicy.isActive());

        feePolicyEntity.delete(userId);

        feePolicyJpaRepository.save(feePolicyEntity);
    }

    @Override
    public Optional<FeePolicy> findByTargetTypeAndTargetKey(TargetType targetType, TargetKey targetKey) {
        return feePolicyJpaRepository.findByTargetTypeAndTargetKey(targetType, targetKey.getValueAsString())
                .map(feePolicyMapper::toDomainEntity);
    }

    @Override
    public Optional<FeePolicy> findApplicablePolicy(Long sellerId, UUID categoryId) {
        return feePolicyJpaRepository.findByTargetTypeAndTargetKey(TargetType.MERCHANT, sellerId.toString())
                .or(() ->
                        feePolicyJpaRepository.findByTargetTypeAndTargetKey(TargetType.CATEGORY, categoryId.toString())
                )
                .map(feePolicyMapper::toDomainEntity);
    }
}

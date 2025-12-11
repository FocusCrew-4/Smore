package com.smore.payment.cancelpolicy.infrastructure.persistence.repository;

import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.domain.model.CancelTargetType;
import com.smore.payment.cancelpolicy.domain.model.TargetKey;
import com.smore.payment.cancelpolicy.domain.repository.CancelPolicyRepository;
import com.smore.payment.cancelpolicy.infrastructure.persistence.mapper.CancelPolicyMapper;
import com.smore.payment.cancelpolicy.infrastructure.persistence.model.CancelPolicyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CancelPolicyRepositoryImpl implements CancelPolicyRepository {

    private final CancelPolicyJpaRepository cancelPolicyJpaRepository;
    private final CancelPolicyMapper cancelPolicyMapper;

    @Override
    public void save(CancelPolicy cancelPolicy) {
        cancelPolicyJpaRepository.save(cancelPolicyMapper.toEntity(cancelPolicy));
    }

    @Override
    public Optional<CancelPolicy> findByTargetTypeAndTargetKey(CancelTargetType cancelTargetType, TargetKey targetKey) {
        return cancelPolicyJpaRepository.findByCancelTargetTypeAndTargetKey(cancelTargetType, targetKey.getValueAsString())
                .map(cancelPolicyMapper::toDomainEntity);
    }

    @Override
    public void delete(CancelPolicy cancelPolicy, Long userId) {
        CancelPolicyEntity entity = cancelPolicyJpaRepository.findById(cancelPolicy.getId())
                .orElseThrow(() -> new IllegalArgumentException("취소 정책을 찾을 수 없습니다."));

        entity.setActive(cancelPolicy.isActive());
        entity.delete(userId);

        cancelPolicyJpaRepository.save(entity);
    }

    @Override
    public Optional<CancelPolicy> findById(UUID id) {
        return cancelPolicyJpaRepository.findById(id)
                .map(cancelPolicyMapper::toDomainEntity);
    }
}

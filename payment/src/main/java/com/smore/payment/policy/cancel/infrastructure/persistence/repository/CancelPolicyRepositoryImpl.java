package com.smore.payment.policy.cancel.infrastructure.persistence.repository;

import com.smore.payment.policy.cancel.domain.model.CancelPolicy;
import com.smore.payment.policy.cancel.domain.model.CancelTargetType;
import com.smore.payment.policy.cancel.domain.model.TargetKey;
import com.smore.payment.policy.cancel.domain.repository.CancelPolicyRepository;
import com.smore.payment.policy.cancel.infrastructure.persistence.mapper.CancelPolicyMapper;
import com.smore.payment.policy.cancel.infrastructure.persistence.model.CancelPolicyEntity;
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

    @Override
    public Optional<CancelPolicy> findApplicablePolicy(Long sellerId, UUID categoryId, String auctionType) {
        return cancelPolicyJpaRepository.findByCancelTargetTypeAndTargetKey(CancelTargetType.MERCHANT, sellerId.toString())
                .or(() ->
                        cancelPolicyJpaRepository.findByCancelTargetTypeAndTargetKey(CancelTargetType.CATEGORY, categoryId.toString())
                )
                .or(() ->
                        cancelPolicyJpaRepository.findByCancelTargetTypeAndTargetKey(CancelTargetType.AUCTION_TYPE, auctionType)
                )
                .map(cancelPolicyMapper::toDomainEntity);
    }
}

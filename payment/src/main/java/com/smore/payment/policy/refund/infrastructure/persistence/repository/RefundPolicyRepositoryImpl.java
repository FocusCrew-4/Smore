package com.smore.payment.policy.refund.infrastructure.persistence.repository;

import com.smore.payment.policy.refund.domain.model.RefundPolicy;
import com.smore.payment.policy.refund.domain.model.RefundTargetType;
import com.smore.payment.policy.refund.domain.model.TargetKey;
import com.smore.payment.policy.refund.domain.repository.RefundPolicyRepository;
import com.smore.payment.policy.refund.infrastructure.persistence.mapper.RefundPolicyMapper;
import com.smore.payment.policy.refund.infrastructure.persistence.model.RefundPolicyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefundPolicyRepositoryImpl implements RefundPolicyRepository {

    private final RefundPolicyJpaRepository refundPolicyJpaRepository;
    private final RefundPolicyMapper refundPolicyMapper;

    @Override
    public void save(RefundPolicy refundPolicy) {
        refundPolicyJpaRepository.save(refundPolicyMapper.toEntity(refundPolicy));
    }

    @Override
    public Optional<RefundPolicy> findByTargetTypeAndTargetKey(RefundTargetType refundTargetType, TargetKey targetKey) {
        return refundPolicyJpaRepository.findByRefundTargetTypeAndTargetKey(refundTargetType, targetKey.getValueAsString())
                .map(refundPolicyMapper::toDomainEntity);
    }

    @Override
    public void delete(RefundPolicy refundPolicy, Long userId) {
        RefundPolicyEntity entity = refundPolicyJpaRepository.findById(refundPolicy.getId())
                .orElseThrow(() -> new IllegalArgumentException("취소 정책을 찾을 수 없습니다."));

        entity.setActive(refundPolicy.isActive());
        entity.delete(userId);

        refundPolicyJpaRepository.save(entity);
    }

    @Override
    public Optional<RefundPolicy> findById(UUID id) {
        return refundPolicyJpaRepository.findById(id)
                .map(refundPolicyMapper::toDomainEntity);
    }

    @Override
    public Optional<RefundPolicy> findApplicablePolicy(Long sellerId, UUID categoryId, String auctionType) {
        return refundPolicyJpaRepository.findByRefundTargetTypeAndTargetKey(RefundTargetType.MERCHANT, sellerId.toString())
                .or(() ->
                        refundPolicyJpaRepository.findByRefundTargetTypeAndTargetKey(RefundTargetType.CATEGORY, categoryId.toString())
                )
                .or(() ->
                        refundPolicyJpaRepository.findByRefundTargetTypeAndTargetKey(RefundTargetType.AUCTION_TYPE, auctionType)
                )
                .map(refundPolicyMapper::toDomainEntity);
    }
}

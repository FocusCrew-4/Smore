package com.smore.order.infrastructure.persistence.repository.refund;

import com.smore.order.application.repository.RefundRepository;
import com.smore.order.domain.model.Refund;
import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;
import com.smore.order.infrastructure.persistence.mapper.RefundMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j(topic = "RefundRepositoryImpl")
@Repository
@RequiredArgsConstructor
public class RefundRepositoryImpl implements RefundRepository {

    private final RefundJpaRepository refundJpaRepository;

    @Override
    public Refund save(Refund refund) {

        RefundEntity entity =  refundJpaRepository.save(
            RefundMapper.toEntityForCreate(refund)
        );

        return RefundMapper.toDomain(entity);
    }

    @Override
    public Refund findByIdempotencyKey(UUID idempotencyKey) {

        RefundEntity entity = refundJpaRepository.findByIdempotencyKey(idempotencyKey);

        if (entity == null) {
            return null;
        }

        return RefundMapper.toDomain(entity);
    }
}

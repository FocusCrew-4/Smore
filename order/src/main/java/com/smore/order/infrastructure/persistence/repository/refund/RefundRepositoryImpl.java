package com.smore.order.infrastructure.persistence.repository.refund;

import com.smore.order.application.repository.RefundRepository;
import com.smore.order.domain.model.Refund;
import com.smore.order.domain.status.RefundStatus;
import com.smore.order.infrastructure.error.OrderErrorCode;
import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;
import com.smore.order.infrastructure.persistence.exception.NotFoundRefundException;
import com.smore.order.infrastructure.persistence.mapper.RefundMapper;
import java.time.LocalDateTime;
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
    public Refund findByIdempotencyKey(UUID idempotencyKey) {

        RefundEntity entity = refundJpaRepository.findByIdempotencyKey(idempotencyKey);

        if (entity == null) {
            return null;
        }

        return RefundMapper.toDomain(entity);
    }

    @Override
    public Refund findById(UUID refundId) {

        RefundEntity entity = refundJpaRepository.findById(refundId).orElseThrow(
            () -> new NotFoundRefundException(OrderErrorCode.NOT_FOUND_REFUND)
        );
        return RefundMapper.toDomain(entity);
    }

    @Override
    public Refund save(Refund refund) {

        RefundEntity entity =  refundJpaRepository.save(
            RefundMapper.toEntityForCreate(refund)
        );

        return RefundMapper.toDomain(entity);
    }

    @Override
    public int complete(UUID refundId, RefundStatus status, LocalDateTime now) {

        return refundJpaRepository.complete(
            refundId, status, now
        );
    }

    @Override
    public int fail(UUID id, RefundStatus refundStatus, String message, LocalDateTime now) {
        return refundJpaRepository.fail(id, refundStatus, message, now);
    }
}

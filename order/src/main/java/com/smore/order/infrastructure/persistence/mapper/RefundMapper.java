package com.smore.order.infrastructure.persistence.mapper;

import com.smore.order.domain.model.Refund;
import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;

public final class RefundMapper {

    private RefundMapper() {

    }

    public static RefundEntity toEntityForCreate(Refund refund) {
        if (refund == null) {
            return null;
        }

        return RefundEntity.create(
            refund.getOrderId(),
            refund.getUserId(),
            refund.getProductId(),
            refund.getRefundQuantity(),
            refund.getRefundAmount(),
            refund.getIdempotencyKey(),
            refund.getReason(),
            refund.getStatus(),
            refund.getRequestedAt()
        );
    }

    public static Refund toDomain(RefundEntity entity) {
        if (entity == null) {
            return null;
        }

        return Refund.of(
            entity.getId(),
            entity.getOrderId(),
            entity.getUserId(),
            entity.getProductId(),
            entity.getRefundQuantity(),
            entity.getRefundAmount(),
            entity.getIdempotencyKey(),
            entity.getReason(),
            entity.getStatus(),
            entity.getRequestedAt(),
            entity.getCompletedAt()
        );
    }
}

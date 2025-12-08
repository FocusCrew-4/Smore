package com.smore.order.infrastructure.persistence.repository.refund;

import com.smore.order.domain.status.RefundStatus;
import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;
import java.time.LocalDateTime;
import java.util.UUID;

public interface RefundJpaRepositoryCustom {

    RefundEntity findByIdempotencyKey(UUID idempotencyKey);

    int complete(UUID refundId, RefundStatus status, LocalDateTime now);

}

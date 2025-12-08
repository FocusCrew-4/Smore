package com.smore.order.infrastructure.persistence.repository.refund;

import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;
import java.util.UUID;

public interface RefundJpaRepositoryCustom {

    RefundEntity findByIdempotencyKey(UUID idempotencyKey);

}

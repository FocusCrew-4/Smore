package com.smore.order.infrastructure.persistence.repository.order;

import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import java.util.UUID;

public interface OrderJpaRepositoryCustom {

    OrderEntity findByIdempotencyKey(UUID idempotencyKey);
}

package com.smore.order.application.repository;

import com.smore.order.domain.model.Order;
import java.util.UUID;

public interface OrderRepository {

    Order findByIdempotencyKey(UUID idempotencyKey);

    Order save(Order order);

}

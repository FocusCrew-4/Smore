package com.smore.order.application.repository;

import com.smore.order.domain.model.Refund;
import java.util.UUID;

public interface RefundRepository {

    Refund save(Refund refund);

    Refund findByIdempotencyKey(UUID idempotencyKey);
}

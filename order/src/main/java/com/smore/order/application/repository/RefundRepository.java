package com.smore.order.application.repository;

import com.smore.order.domain.model.Refund;
import com.smore.order.domain.status.RefundStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public interface RefundRepository {

    Refund save(Refund refund);

    Refund findByIdempotencyKey(UUID idempotencyKey);

    Refund findById(UUID refundId);

    int complete(UUID refundId, RefundStatus status, LocalDateTime now);
}

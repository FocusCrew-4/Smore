package com.smore.payment.payment.infrastructure.persistence.inbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefundInboxRepository extends JpaRepository<RefundInbox, Long> {

    Optional<RefundInbox> findByRefundId(UUID refundId);
}

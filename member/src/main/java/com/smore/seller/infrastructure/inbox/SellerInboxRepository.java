package com.smore.seller.infrastructure.inbox;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerInboxRepository extends JpaRepository<SellerInbox, UUID> {

    boolean existsByIdempotencyKey(UUID idempotencyKey);
}

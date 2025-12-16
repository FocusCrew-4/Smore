package com.smore.auction.infrastructure.inbox;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InboxRepository extends JpaRepository<InboxJpa, UUID> {

    @Modifying
    @Query(
        value = """
            INSERT INTO p_auction_inbox (idempotency_key)
            VALUES (:key)
            ON CONFLICT (idempotency_key) DO NOTHING
            """,
        nativeQuery = true
    )
    int idempotencyKeyUpsert(@Param("key") UUID key);
}

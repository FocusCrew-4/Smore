package com.smore.auction.infrastructure.outbox;

import com.smore.auction.infrastructure.outbox.AuctionOutbox.MessageStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AuctionOutboxRepository extends JpaRepository<AuctionOutbox, Long> {

    List<AuctionOutbox> findTop100ByStatusOrderByIdAsc(MessageStatus messageStatus);

    @Modifying
    @Query("""
        update AuctionOutbox o
        set o.status = 'SENT',
        o.processedAt = :now,
        o.errorMessage = null 
        where o.id = :id
        """)
    void markSent(Long id, LocalDateTime now);

    @Modifying
    @Query("""
        update AuctionOutbox o
        set o.retryCount = :retry,
        o.errorMessage = :message,
        o.processedAt = :now
        where o.id = :id
        """)
    void updateRetry(Long id, int retry, String message, LocalDateTime now);

    @Modifying
    @Query("""
        update AuctionOutbox o
        set o.status = 'FAILED',
        o.errorMessage = :message,
        o.processedAt = :now
        where o.id = :id
        """)
    void markFailed(Long id, String message, LocalDateTime now);

}

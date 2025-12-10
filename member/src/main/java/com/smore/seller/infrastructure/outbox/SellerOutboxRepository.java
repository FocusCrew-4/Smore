package com.smore.seller.infrastructure.outbox;

import com.smore.seller.infrastructure.outbox.SellerOutbox.MessageStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SellerOutboxRepository extends CrudRepository<SellerOutbox, Long> {

    List<SellerOutbox> findTop100ByStatusOrderByIdAsc(
        MessageStatus messageStatus
    );

    // batchSize 를 동적으로 변환하고 싶다면 사용
    List<SellerOutbox> findByStatusOrderByIdAsc(
        SellerOutbox.MessageStatus status,
        Pageable pageable
    );

    // TODO: 추후 쿼리dsl 로 이관 필요
    @Modifying
    @Query("UPDATE SellerOutbox o SET o.status = 'SENT', o.processedAt = :processedAt, o.errorMessage = null WHERE o.id = :id")
    void markSent(Long id, LocalDateTime processedAt);

    @Modifying
    @Query("UPDATE SellerOutbox o SET o.retryCount = :retry, o.errorMessage = :error, o.processedAt = :processedAt WHERE o.id = :id")
    void updateRetry(Long id, int retry, String error, LocalDateTime processedAt);

    @Modifying
    @Query("UPDATE SellerOutbox o SET o.status = 'FAILED', o.errorMessage = :error, o.processedAt = :processedAt WHERE o.id = :id")
    void markFailed(Long id, String error, LocalDateTime processedAt);

}

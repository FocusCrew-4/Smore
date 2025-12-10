package com.smore.seller.infrastructure.outbox;

import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SellerOutboxDispatcher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SellerOutboxRepository repository;
    private final Clock clock;

    private static final int MAX_RETRY = 3;

    @Transactional
    public void dispatch(SellerOutbox event) {
        try {
            var future = kafkaTemplate.send(
                event.getEventType(),
                event.getMemberId().toString(),
                event.getPayload()
            );

            future.get();

            repository.markSent(
                event.getId(),
                LocalDateTime.now(clock)
            );
        } catch (Exception e) {
            handleFailure(event, e);
        }
    }
    private void handleFailure(SellerOutbox event, Exception e) {

        int retry = event.getRetryCount() + 1;
        if (retry >= MAX_RETRY) {
            repository.markFailed(
                event.getId(),
                e.getMessage(),
                LocalDateTime.now(clock)
            );
        } else {
            repository.updateRetry(
                event.getId(),
                retry,
                e.getMessage(),
                LocalDateTime.now(clock)
            );
        }
    }
}

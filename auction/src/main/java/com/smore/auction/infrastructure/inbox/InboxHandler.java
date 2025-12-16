package com.smore.auction.infrastructure.inbox;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class InboxHandler {
    private final InboxRepository inboxRepository;

    public void processOnce(UUID idempotencyKey, Runnable function) {
            int duplicate = inboxRepository.idempotencyKeyUpsert(idempotencyKey);

            if (duplicate == 0) {
                return;
            }

            function.run();
    }
}

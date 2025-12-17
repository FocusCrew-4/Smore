package com.smore.auction.infrastructure.inbox;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class InboxHandler {
    private final InboxRepository inboxRepository;

    public void processOnce(UUID idempotencyKey, Runnable function) {
            int duplicate = inboxRepository.idempotencyKeyUpsert(idempotencyKey);

            if (duplicate == 0) {
                log.warn("이미 존재하는 멱등키 입니다");
                return;
            }

            function.run();
    }
}

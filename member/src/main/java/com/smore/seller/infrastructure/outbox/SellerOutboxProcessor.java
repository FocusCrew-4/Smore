package com.smore.seller.infrastructure.outbox;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SellerOutboxProcessor {

    private final SellerOutboxFetcher fetcher;
    private final SellerOutboxDispatcher dispatcher;

    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedDelay = 2000)
    public void process() {
        List<SellerOutbox> events = fetcher.fetchPending();
        for (SellerOutbox event : events) {
            try {
                dispatcher.dispatch(event);
            } catch (Exception e) {
                // 절대 throw 하지 않음
                log.error("[Outbox] Unexpected error while processing id={}", event.getId(), e);
            }
        }
    }

}

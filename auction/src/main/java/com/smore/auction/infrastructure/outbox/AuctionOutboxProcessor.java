package com.smore.auction.infrastructure.outbox;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuctionOutboxProcessor {

    private final AuctionOutboxFetcher fetcher;
    private final AuctionOutboxDispatcher dispatcher;

    @Scheduled(fixedDelay = 2000)
    public void process() {
        List<AuctionOutbox> events = fetcher.fetchPending();
        for (AuctionOutbox event : events) {
            try {
                dispatcher.dispatch(event);
            } catch (Exception e) {
                log.error("[Outbox] dispatch Error: {}", event.getId(), e);
            }
        }
    }

}

package com.smore.auction.infrastructure.outbox;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuctionOutboxProcessor {

    private final AuctionOutboxFetcher fetcher;
    private final AuctionOutboxDispatcher dispatcher;
    private final ObservationRegistry observationRegistry;

    @Scheduled(fixedDelay = 2000)
    public void process() {
        List<AuctionOutbox> events = fetcher.fetchPending();
        if (events.isEmpty()) {
            return;
        }

        Observation observation = Observation.start("outbox.dispatch", observationRegistry);
        observation.lowCardinalityKeyValue("outbox.count", String.valueOf(events.size()));
        try (Observation.Scope scope = observation.openScope()) {
            for (AuctionOutbox event : events) {
                try {
                    dispatcher.dispatch(event);
                } catch (Exception e) {
                    log.error("[Outbox] dispatch Error: {}", event.getId(), e);
                }
            }
        } finally {
            observation.stop();
        }
    }

}

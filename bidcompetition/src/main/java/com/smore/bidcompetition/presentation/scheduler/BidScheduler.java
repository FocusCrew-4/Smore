package com.smore.bidcompetition.presentation.scheduler;

import com.smore.bidcompetition.application.service.BidProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j(topic = "BidScheduler")
@Service
@RequiredArgsConstructor
public class BidScheduler {

    private final BidProcessor bidProcessor;

    @Scheduled(cron = "0 */5 * * * *")
    public void bidLifecycleScheduler() {

        bidProcessor.active();
        bidProcessor.close();
        bidProcessor.end();

    }
}

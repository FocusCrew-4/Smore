package com.smore.bidcompetition.presentation.scheduler;

import com.smore.bidcompetition.application.service.BidProcessor;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // TODO: 페이지 스킵 발생하므로 이를 해결해야 함
    // TODO: @Async 비동기 예외 모니터링/재시도 처리 추가
    @Scheduled(fixedDelay = 10_000)
    public void recoveryExpiredStockScheduler() {
        int page = 0;
        int pageSize = 50;

        while (true) {
            Page<UUID> taskIds = bidProcessor.getExpiredWinnerIds(
                PageRequest.of(page, pageSize)
            );

            if (!taskIds.hasContent()) break;


            for (UUID winnerId : taskIds) {
                try {
                    bidProcessor.recoveryStock(winnerId);
                } catch (Exception e) {
                    log.error("task 스케줄러 작업 위임 실패 {}", winnerId, e);
                }
            }

            if (!taskIds.hasNext()) break;
            page++;
        }
    }
}

package com.smore.order.presentation.scheduler;

import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.application.service.OutboxProcessor;
import com.smore.order.domain.status.EventStatus;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j(topic = "OutboxScheduler")
@Service
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final OutboxProcessor processor;

    private static final Set<EventStatus> PROCESSING_STATES = Set.of(
        EventStatus.PENDING
    );

    @Scheduled(fixedDelay = 100)
    public void outboxTasks() {
        int page = 0;
        int pageSize = 100;

        while (true) {
            Page<Long> taskIds = outboxRepository.findPendingIds(
                PROCESSING_STATES,
                PageRequest.of(page, pageSize)
            );

            if (!taskIds.hasContent()) break;

            for (Long outboxId : taskIds) {
                try {
                    processor.outboxProcessor(outboxId);
                } catch (Exception e) {
                    log.error("task 스케줄러 작업 위임 실패 {}", outboxId, e);
                }
            }

            if (!taskIds.hasNext()) break;
            page++;
        }
    }
}

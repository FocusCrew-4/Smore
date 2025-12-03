package com.smore.order.application.service;

import com.smore.order.application.factory.OutboxCommandFactory;
import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.application.command.OutboxCommand;
import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.EventStatus;
import com.smore.order.domain.status.OutboxResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j(topic = "OutboxProcessor")
@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    @Value("${retry.count.max}")
    private Integer maxRetryCount;

    private final OutboxRepository outboxRepository;
    private final OutboxCommandFactory factory;

    @Async("taskExecutor")
    public void outboxProcessor(Long outboxId) {

        int updated = outboxRepository.claim(outboxId, EventStatus.PROCESSING);
        if (updated == 0) {
            return;
        }

        Outbox fresh = outboxRepository.findById(outboxId);

        if (fresh.isExceededRetry(maxRetryCount)) {
            int result = outboxRepository.makeFail(fresh.getId(), EventStatus.FAILED, maxRetryCount);
            if (result == 0) {
                log.error("재시도 횟수 초과 후, outbox 상태 전환 실패 outboxId : {}, domain : {}, eventType : {}",
                    fresh.getId(), fresh.getAggregateType(), fresh.getEventType());
            }
            return;
        }

        OutboxCommand command = factory.from(fresh);
        OutboxResult result = command.execute();

        // FIXME : 선점 상태이므로 상태 전이가 적용되지 않는다면 영원히 PROGRESS 상태가 됨 이에 대한 후속 처리 필요
        if (result == OutboxResult.SUCCESS) {
            updated = outboxRepository.markSent(fresh.getId(), EventStatus.SENT);
            if (updated == 0) {
                log.error("카프카에 이벤트 발행 후, outbox 상태 전환 실패 outboxId : {}, domain : {}, eventType : {}",
                    fresh.getId(), fresh.getAggregateType(), fresh.getEventType());
                return;
            }
        } else {
            updated = outboxRepository.makeRetry(fresh.getId(), EventStatus.PENDING);
            if (updated == 0) {
                log.error("카프카에 이벤트 발행 실패 후, outbox 상태 전환 실패 outboxId : {}, domain : {}, eventType : {}",
                    fresh.getId(), fresh.getAggregateType(), fresh.getEventType());
                return;
            }
        }
    }

}

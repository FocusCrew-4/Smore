package com.smore.payment.payment.infrastructure.kafka;

import com.smore.payment.global.outbox.OutboxStatus;
import com.smore.payment.payment.infrastructure.persistence.jpa.model.outbox.OutboxEntity;
import com.smore.payment.payment.infrastructure.persistence.jpa.repository.outbox.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentApprovedOutboxPublisher {

    private final OutboxJpaRepository outboxJpaRepository;
    private final MessageBrokerPublisher messageBrokerPublisher;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publish() {
        log.info("이벤트 발행 시작");
        List<OutboxEntity> pendingMessages = outboxJpaRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        log.info("아웃박스 찾아오기 {}", pendingMessages.size());
        for (OutboxEntity message : pendingMessages) {
            try {
                messageBrokerPublisher.publish(
                        message.getTopicName(),
                        message.getPayload(),
                        message.getAggregateId().toString()
                );

                message.markAsSent();
                outboxJpaRepository.save(message);

            } catch (Exception e) {

                if (message.getRetryCount() <= 1) {
                    message.markAsFailed();
                } else {
                    message.decreaseRetryCount();
                }

                outboxJpaRepository.save(message);
                log.error("Outbox message publish failed: {}", message.getId(), e);
            }
        }
    }
}

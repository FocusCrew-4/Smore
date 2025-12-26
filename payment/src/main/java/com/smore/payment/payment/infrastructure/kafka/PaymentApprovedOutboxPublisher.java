package com.smore.payment.payment.infrastructure.kafka;

import com.smore.payment.shared.outbox.OutboxStatus;
import com.smore.payment.payment.infrastructure.persistence.outbox.OutboxEntity;
import com.smore.payment.payment.infrastructure.persistence.outbox.OutboxJpaRepository;
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
    public void publish() {
        log.info("이벤트 발행 시작");
        List<OutboxEntity> pendingMessages = outboxJpaRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        log.info("아웃박스 찾아오기 {}", pendingMessages.size());
        send(pendingMessages);
    }

    @Scheduled(fixedDelay = 30000)
    public void retry() {
        log.info("이벤트 재전송 시작");
        List<OutboxEntity> pendingMessages = outboxJpaRepository.findTop50ByStatusAndRetryCountGreaterThanOrderByCreatedAtAsc(OutboxStatus.FAILED, 0);
        log.info("재전송 아웃박스 찾아오기 {}", pendingMessages.size());
        send(pendingMessages);
    }

    private void send(List<OutboxEntity> pendingMessages) {
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

                if (message.getRetryCount() < 1) {
                    message.markAsFailed();
                    message.resetRetryCount();
                    log.warn("out box 재전송 실패 : {}", message.getId());
                } else {
                    message.decreaseRetryCount();
                }

                outboxJpaRepository.save(message);
                log.error("Outbox message publish failed: {}", message.getId(), e);
            }
        }
    }




}

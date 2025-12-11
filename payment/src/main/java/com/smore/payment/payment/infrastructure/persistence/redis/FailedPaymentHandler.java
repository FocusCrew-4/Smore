package com.smore.payment.payment.infrastructure.persistence.redis;

import com.smore.payment.global.outbox.OutboxMessage;
import com.smore.payment.global.outbox.OutboxMessageCreator;
import com.smore.payment.payment.application.event.outbound.PaymentFailedEvent;
import com.smore.payment.payment.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailedPaymentHandler {

    private final OutboxMessageCreator outboxMessageCreator;
    private final OutboxRepository outboxRepository;

    public void handleExpiredKey(String key) {

        UUID orderId = UUID.fromString(key);

        log.info("임시결제 TTL 만료 → 결제 실패 처리 orderId={}", orderId);

        PaymentFailedEvent event = new PaymentFailedEvent(
                orderId,
                "결제 기간이 만료되었습니다."
        );

        // Outbox 생성 → 저장
        OutboxMessage outboxMessage = outboxMessageCreator.paymentFailed(event);
        outboxRepository.save(outboxMessage);
    }
}

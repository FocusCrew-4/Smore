package com.smore.payment.payment.infrastructure.persistence.inbox;

import com.smore.payment.payment.application.event.outbound.PaymentRefundFailedEvent;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.infrastructure.kafka.dto.PaymentRefundRequestEvent;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundInboxProcessor {

    private final RefundInboxRepository refundInboxRepository;
    private final OutboxPort outboxPort;
    private final OutboxMessageCreator outboxMessageCreator;

    private static final Integer MAX_RETRY = 3;


    @Transactional
    public boolean startOrSkip(PaymentRefundRequestEvent event) {
        RefundInbox inbox = refundInboxRepository.findByRefundId(event.getRefundId())
                .orElseGet(() -> {
                    try {
                        return refundInboxRepository.save(RefundInbox.create(event));
                    } catch (DataIntegrityViolationException e) {
                        return refundInboxRepository.findByRefundId(event.getRefundId())
                                .orElseThrow(() -> e);
                    }
                });

        // 이미 완료면 처리할 필요 없음
        if (inbox.isFinalized()) {
            return false;
        }

        // 이미 누가 처리 중이면 skip
        if (inbox.isProcessing()) {
            return false;
        }

        if (inbox.getRetryCount() >= MAX_RETRY) {
            log.error("재시도 횟수 한계. refundId={}", event.getRefundId());

            publishRefundDlt(event, inbox);
            return false;
        }

        try {
            inbox.markProcessing();
            refundInboxRepository.save(inbox);
            return true;
        } catch (ObjectOptimisticLockingFailureException e) {
            return false;
        }
    }

    private void publishRefundDlt(PaymentRefundRequestEvent event, RefundInbox inbox) {

        String reason = String.format(
                "환불 자동 재시도 한계 초과 (retryCount=%d)",
                inbox.getRetryCount()
        );


        outboxPort.save(
                outboxMessageCreator.refundDlt(
                        PaymentRefundFailedEvent.of(
                                event.getOrderId(),
                                event.getRefundId(),
                                BigDecimal.valueOf(event.getRefundAmount()),
                                reason
                        )
                )
        );

        log.error("Refund moved to DLT. refundId={}, reason={}",
                event.getRefundId(), reason);
    }
}

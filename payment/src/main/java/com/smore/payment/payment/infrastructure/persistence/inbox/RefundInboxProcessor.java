package com.smore.payment.payment.infrastructure.persistence.inbox;

import com.smore.payment.payment.infrastructure.kafka.dto.PaymentRefundRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefundInboxProcessor {

    private final RefundInboxRepository refundInboxRepository;

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

        try {
            inbox.markProcessing();
            refundInboxRepository.save(inbox);
            return true;
        } catch (ObjectOptimisticLockingFailureException e) {
            return false;
        }
    }
}

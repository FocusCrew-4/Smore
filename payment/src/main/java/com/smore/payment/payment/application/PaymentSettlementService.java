package com.smore.payment.payment.application;

import com.smore.payment.payment.application.event.inbound.PaymentSettlementRequestEvent;
import com.smore.payment.payment.application.event.outbound.SettlementFailedEvent;
import com.smore.payment.payment.application.event.outbound.SettlementSuccessEvent;
import com.smore.payment.payment.application.port.in.SettlePaymentUseCase;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.domain.repository.SellerSettlementLedgerRepository;
import com.smore.payment.payment.domain.service.SettlementValidationResult;
import com.smore.payment.payment.domain.service.SettlementValidationService;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSettlementService implements SettlePaymentUseCase {

    private final SellerSettlementLedgerRepository sellerSettlementLedgerRepository;
    private final OutboxPort outboxPort;
    private final OutboxMessageCreator outboxCreator;
    private final SettlementValidationService settlementValidationService;

    @Transactional
    @Override
    public void settle(PaymentSettlementRequestEvent event) {

        SettlementValidationResult validation = settlementValidationService.validate(event);
        if (validation.duplicated()) {
            return;
        }

        if (!validation.valid()) {
            outboxPort.save(
                    outboxCreator.settlementFailed(
                            SettlementFailedEvent.of(
                                    event.userId(),
                                    event.amount(),
                                    event.accountNumber(),
                                    event.idempotencyKey(),
                                    validation.failureReason()
                            )
                    )
            );
            return;
        }

        try {
            sellerSettlementLedgerRepository.saveLedger(
                    event.userId(),
                    "SETTLEMENT",
                    event.amount(),
                    null,
                    event.idempotencyKey()
            );

            outboxPort.save(
                    outboxCreator.settlementCompleted(
                            SettlementSuccessEvent.of(
                                    event.userId(),
                                    event.amount(),
                                    event.accountNumber(),
                                    event.idempotencyKey()
                            )
                    )
            );

        } catch (Exception e) {

            outboxPort.save(
                    outboxCreator.settlementDlt(
                            SettlementFailedEvent.of(
                                    event.userId(),
                                    event.amount(),
                                    event.accountNumber(),
                                    event.idempotencyKey(),
                                    "정산 처리 중 시스템 오류 발생: " + e.getMessage()
                            )
                    )
            );

            throw new RuntimeException("정산 처리 중 시스템 오류 발생", e);
        }
    }


}

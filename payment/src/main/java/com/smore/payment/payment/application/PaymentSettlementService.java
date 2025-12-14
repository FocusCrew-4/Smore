package com.smore.payment.payment.application;

import com.smore.payment.global.outbox.OutboxMessageCreator;
import com.smore.payment.payment.application.event.inbound.PaymentSettlementRequestEvent;
import com.smore.payment.payment.application.event.outbound.SettlementFailedEvent;
import com.smore.payment.payment.application.event.outbound.SettlementSuccessEvent;
import com.smore.payment.payment.domain.repository.OutboxRepository;
import com.smore.payment.payment.domain.repository.SellerSettlementLedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSettlementService {

    private final SellerSettlementLedgerRepository sellerSettlementLedgerRepository;
    private final OutboxRepository outboxRepository;
    private final OutboxMessageCreator outboxCreator;

    @Transactional
    public void settlement(PaymentSettlementRequestEvent event) {

        // 1) 멱등성 체크
        if (sellerSettlementLedgerRepository.existsByIdempotencyKey(event.idempotencyKey())) {
            return;
        }

        // 2) 계좌번호 검증 – 비즈니스 실패 → Retry 불필요
        if (event.accountNumber() == null || event.accountNumber().isBlank()) {
            outboxRepository.save(
                    outboxCreator.settlementFailed(
                            SettlementFailedEvent.of(
                                    event.userId(),
                                    event.amount(),
                                    event.accountNumber(),
                                    event.idempotencyKey(),
                                    "계좌 번호가 잘못 입력되었습니다."
                            )
                    )
            );
            return;
        }

        // 3) 잔액 검증 – 비즈니스 실패 → Retry 불필요
        BigDecimal balance = sellerSettlementLedgerRepository.calculateBalance(event.userId());

        if (balance.compareTo(event.amount()) < 0
                || balance.compareTo(BigDecimal.valueOf(1_000_000_000L)) > 0) {

            outboxRepository.save(
                    outboxCreator.settlementFailed(
                            SettlementFailedEvent.of(
                                    event.userId(),
                                    event.amount(),
                                    event.accountNumber(),
                                    event.idempotencyKey(),
                                    "출금 가능 잔액이 부족합니다."
                            )
                    )
            );
            return;
        }

        // 4) 시스템 오류 가능 구간 – Retry + DLT 대상
        try {
            sellerSettlementLedgerRepository.saveLedger(
                    event.userId(),
                    "SETTLEMENT",
                    event.amount(),
                    null,
                    event.idempotencyKey()
            );

            outboxRepository.save(
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

            // (1) 내부 DLT logging 저장
            outboxRepository.save(
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

            // (2) Kafka RetryTopic → DLT 로 넘어가도록 강제 예외 전파
            throw new RuntimeException("정산 처리 중 시스템 오류 발생", e);
        }
    }


}

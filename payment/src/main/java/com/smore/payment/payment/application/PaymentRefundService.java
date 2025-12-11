package com.smore.payment.payment.application;

import com.smore.payment.global.outbox.OutboxMessage;
import com.smore.payment.global.outbox.OutboxMessageCreator;
import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundFailedEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundSucceededEvent;
import com.smore.payment.payment.application.facade.CancelPolicyFacade;
import com.smore.payment.payment.application.facade.RefundPolicyFacade;
import com.smore.payment.payment.application.facade.dto.CancelPolicyResult;
import com.smore.payment.payment.application.facade.dto.RefundPolicyResult;
import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.repository.OutboxRepository;
import com.smore.payment.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentRefundService {

    private final PaymentRepository paymentRepository;
    private final CancelPolicyFacade cancelPolicyFacade;
    private final RefundPolicyFacade refundPolicyFacade;
    private final PgClient pgClient;
    private final OutboxMessageCreator outboxCreator;
    private final OutboxRepository outboxRepository;

    public void refund(PaymentRefundEvent event) {

        //
        // 1) 결제 조회
        //
        Payment payment = paymentRepository.findById(event.paymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. paymentId=" + event.paymentId()));

        //
        // 2) 취소 정책 조회
        //
        CancelPolicyResult cancelResult = cancelPolicyFacade.findApplicablePolicy(
                payment.getSellerId(),
                payment.getCategoryId(),
                payment.getAuctionType()
        );

        //
        // 3) 취소 정책 검증
        //
        boolean cancelAvailable = cancelResult.cancellable(payment.getApprovedAt(), event.publishedAt());
        RefundPolicyResult refundResult = null;
        if (!cancelAvailable) {
            refundResult = refundPolicyFacade.findApplicablePolicy(
                    payment.getSellerId(),
                    payment.getCategoryId(),
                    payment.getAuctionType()
            );

            if (!refundResult.refundable(payment.getApprovedAt(), event.publishedAt())) {
                OutboxMessage failedMsg = outboxCreator.paymentRefundFailed(
                        PaymentRefundFailedEvent.of(event.orderId(), event.refundId(), event.refundAmount(), "환불이 불가능 한 상품입니다.")
                );
                outboxRepository.save(failedMsg);
                return;
            }
        }

        //
        // 5) 최종 환불 금액 계산
        //
        final BigDecimal refundAmount = (cancelAvailable)
                ? cancelResult.calculateCancelFee(event.refundAmount())
                : refundResult.calculateRefundFee(event.refundAmount());

        //
        // 6) PG 환불 호출
        //
        PgResponseResult pgRefundResult;

        try {
            pgRefundResult = pgClient.refund(
                    payment.getPaymentKey(),
                    refundAmount,
                    event.refundReason()
            );

        } catch (Exception e) {

            // PG 실패 → 환불 실패 이벤트 Outbox 발행
            OutboxMessage failedMsg = outboxCreator.paymentRefundFailed(
                    PaymentRefundFailedEvent.of(event.orderId(), event.refundId(), refundAmount, e.getMessage())
            );
            outboxRepository.save(failedMsg);

            throw e;
        }

        //
        // 7) Payment 엔티티 상태 업데이트
        //
        payment.updateRefund(
                pgRefundResult.cancels().cancelReason(),
                event.refundAmount(),
                pgRefundResult.cancels().canceledAt(),
                pgRefundResult.cancels().cancelTransactionKey(),
                pgRefundResult.cancels().cancelAmount()
        );
        paymentRepository.updateRefund(payment.getId(), payment.getRefund());

        // Todo: 환불 후 정산금 어떻게 하지..........
        //
        // 8) 환불 성공 이벤트 Outbox 저장
        //
        OutboxMessage successMsg = outboxCreator.paymentRefunded(
                PaymentRefundSucceededEvent.of(event.orderId(), event.refundId(), refundAmount)
        );

        outboxRepository.save(successMsg);
    }
}

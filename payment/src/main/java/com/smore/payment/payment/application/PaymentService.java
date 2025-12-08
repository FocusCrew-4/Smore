package com.smore.payment.payment.application;

import com.smore.payment.global.outbox.OutboxMessage;
import com.smore.payment.payment.application.command.ApprovePaymentCommand;
import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.domain.event.PaymentApprovedEvent;
import com.smore.payment.payment.domain.event.PaymentFailedEvent;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgApproveResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.repository.MongoRepository;
import com.smore.payment.payment.domain.repository.OutboxRepository;
import com.smore.payment.payment.domain.repository.PaymentRepository;
import com.smore.payment.payment.domain.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final RedisRepository redisRepository;
    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final PgClient pgClient;
    private final MongoRepository mongoRepository; 

    public Payment approve(ApprovePaymentCommand command) {

        // 1. 임시 결제 조회
        TemporaryPayment temp = redisRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new IllegalStateException("임시 결제를 찾을 수 없습니다."));

        // 2. 검증
        temp.validateApproval(command.amount());

        PgApproveResult result;

        try {
            // 3. PG 승인 요청
            result = pgClient.approve(
                    command.paymentKey(),
                    command.pgOrderId(),
                    command.amount()
            );

        } catch (Exception e) {

            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    temp.getOrderId(),
                    command.paymentKey(),
                    e.getMessage()
            );

            outboxRepository.save(
                    OutboxMessage.paymentFailed(failedEvent)
            );


            redisRepository.deleteByOrderId(temp.getOrderId());

            // Todo: 로그 저장

            throw e;
        }

        Payment payment = Payment.createFinalPayment(
                temp.getIdempotencyKey(),
                temp.getUserId(),
                temp.getOrderId(),
                temp.getAmount(),
                temp.getMethod(),
                result
        );

        paymentRepository.save(payment);

        PaymentApprovedEvent event = new PaymentApprovedEvent(
                payment.getOrderId(),
                payment.getId(),
                payment.getAmount(),
                payment.getApprovedAt()
        );

        outboxRepository.save(
                OutboxMessage.paymentApproved(event)
        );

        redisRepository.deleteByOrderId(temp.getOrderId());

        // Todo: 로그 저장

        return payment;
    }
}


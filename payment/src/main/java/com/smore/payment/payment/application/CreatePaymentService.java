package com.smore.payment.payment.application;

import com.smore.payment.global.outbox.OutboxMessage;
import com.smore.payment.global.outbox.OutboxMessageCreator;
import com.smore.payment.payment.application.command.ApprovePaymentCommand;
import com.smore.payment.payment.application.event.outbound.SettlementCalculatedEvent;
import com.smore.payment.payment.application.facade.FeePolicyFacade;
import com.smore.payment.payment.application.facade.dto.FeePolicyResult;
import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.application.event.outbound.PaymentApprovedEvent;
import com.smore.payment.payment.domain.model.Payment;
//import com.smore.payment.payment.domain.document.PgApproveLog;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.repository.MongoRepository;
import com.smore.payment.payment.domain.repository.OutboxRepository;
import com.smore.payment.payment.domain.repository.PaymentRepository;
import com.smore.payment.payment.domain.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreatePaymentService {

    private final RedisRepository redisRepository;
    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final PgClient pgClient;
    private final MongoRepository mongoRepository;
    private final FeePolicyFacade feePolicyFacade;
    private final OutboxMessageCreator outboxMessageCreator;

    public Payment approve(ApprovePaymentCommand command) {

        TemporaryPayment temp = redisRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new IllegalStateException("임시 결제를 찾을 수 없습니다."));

        temp.validateApproval(command.amount());

        paymentRepository.findByIdempotencyKey(temp.getIdempotencyKey())
                .ifPresent(existing -> {
                    throw new IllegalStateException("이미 결제가 승인되었습니다.");
                });

        PgResponseResult result;

        try {
            result = pgClient.approve(
                    command.paymentKey(),
                    command.pgOrderId(),
                    command.amount()
            );

            // Todo: 로그 document 추가
//            PgApproveLog log = PgApproveLog.builder().build();
//            mongoRepository.savePgApproveLog(log);

        } catch (Exception e) {

//            PgApproveLog log = PgApproveLog.builder().build();
//            mongoRepository.savePgApproveLog(log);

            // Todo: 예외 반환

            throw e;
        }

        Payment payment = Payment.createFinalPayment(
                temp.getIdempotencyKey(),
                temp.getUserId(),
                temp.getOrderId(),
                temp.getAmount(),
                temp.getSellerId(),
                temp.getCategoryId(),
                temp.getAuctionType(),
                result
        );

        paymentRepository.save(payment);

        PaymentApprovedEvent event = new PaymentApprovedEvent(
                payment.getOrderId(),
                payment.getId(),
                payment.getAmount(),
                payment.getApprovedAt(),
                payment.getIdempotencyKey()
        );

        FeePolicyResult policy = feePolicyFacade.findApplicablePolicy(
                payment.getSellerId(),
                payment.getCategoryId()
        );

        BigDecimal settlementAmount = calculateSettlementAmount(payment.getAmount(), policy);

        SettlementCalculatedEvent settlementEvent = new SettlementCalculatedEvent(
                payment.getSellerId(),
                settlementAmount,
                payment.getIdempotencyKey()
        );

        OutboxMessage paymentApprovedMsg = outboxMessageCreator.paymentApproved(event);
        OutboxMessage settlementCalculatedMsg = outboxMessageCreator.settlementCalculated(settlementEvent);

        outboxRepository.save(paymentApprovedMsg);

        outboxRepository.save(settlementCalculatedMsg);

        redisRepository.deleteByOrderId(temp.getOrderId());

        return payment;
    }

    private BigDecimal calculateSettlementAmount(BigDecimal approvedAmount, FeePolicyResult policy) {

        BigDecimal fee;

        switch (policy.feeType()) {
            case "RATE" -> fee = approvedAmount.multiply(policy.rate());

            case "FIXED" -> fee = policy.fixedAmount();

            case "MIXED" -> fee = approvedAmount.multiply(policy.rate())
                    .add(policy.fixedAmount());
            default -> throw new IllegalArgumentException("Unknown feeType: " + policy.feeType());
        }

        return approvedAmount.subtract(fee);
    }

}


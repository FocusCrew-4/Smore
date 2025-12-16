package com.smore.payment.payment.application;

import com.smore.payment.payment.application.facade.FeePolicyFacade;
import com.smore.payment.payment.application.facade.dto.FeePolicyResult;
import com.smore.payment.payment.application.port.in.ApprovePaymentCommand;
import com.smore.payment.payment.application.port.in.ApprovePaymentResult;
import com.smore.payment.payment.application.port.in.ApprovePaymentUseCase;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.application.port.out.PaymentRepository;
import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.application.port.out.TemporaryPaymentPort;
import com.smore.payment.payment.domain.event.PaymentApprovedEvent;
import com.smore.payment.payment.domain.event.SettlementCalculatedEvent;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.service.SettlementAmountCalculator;
import com.smore.payment.shared.outbox.OutboxMessage;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApprovePaymentService implements ApprovePaymentUseCase {

    private final TemporaryPaymentPort temporaryPaymentPort;
    private final PaymentRepository paymentRepository;
    private final OutboxPort outboxPort;
    private final PgClient pgClient;
    private final FeePolicyFacade feePolicyFacade;
    private final OutboxMessageCreator outboxMessageCreator;
    private final SettlementAmountCalculator settlementAmountCalculator;

    @Override
    public ApprovePaymentResult approve(ApprovePaymentCommand command) {

        TemporaryPayment temp = temporaryPaymentPort.findByOrderId(command.orderId())
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

        FeePolicyResult policy = feePolicyFacade.findApplicablePolicy(
                payment.getSellerId(),
                payment.getCategoryId()
        );

        PaymentApprovedEvent event = payment.createApprovedEvent();
        SettlementCalculatedEvent settlementEvent = payment.createSettlementCalculatedEvent( settlementAmountCalculator.calculate(payment.getAmount(), policy));

        OutboxMessage paymentApprovedMsg = outboxMessageCreator.paymentApproved(event);
        OutboxMessage settlementCalculatedMsg = outboxMessageCreator.settlementCalculated(settlementEvent);

        outboxPort.save(paymentApprovedMsg);

        outboxPort.save(settlementCalculatedMsg);

        temporaryPaymentPort.deleteByOrderId(temp.getOrderId());

        return new ApprovePaymentResult(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getApprovedAt(),
                payment.getStatus().name()
        );
    }


}


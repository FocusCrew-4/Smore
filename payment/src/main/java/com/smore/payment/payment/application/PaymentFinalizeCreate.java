package com.smore.payment.payment.application;

import com.smore.payment.payment.application.facade.FeePolicyFacade;
import com.smore.payment.payment.application.facade.dto.FeePolicyResult;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.application.port.out.PaymentRepository;
import com.smore.payment.payment.application.port.out.SellerSettlementLedgerRepository;
import com.smore.payment.payment.application.port.out.TemporaryPaymentPort;
import com.smore.payment.payment.domain.event.SettlementCalculatedEvent;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.service.SettlementAmountCalculator;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFinalizeCreate {

    private final PaymentRepository paymentRepository;
    private final FeePolicyFacade feePolicyFacade;
    private final SellerSettlementLedgerRepository sellerSettlementLedgerRepository;
    private final TemporaryPaymentPort temporaryPaymentPort;

    private final SettlementAmountCalculator settlementAmountCalculator;

    private final OutboxPort outboxPort;
    private final OutboxMessageCreator outboxMessageCreator;

    @Transactional
    public Payment finalizePayment(
            TemporaryPayment temp,
            PgResponseResult result
    ) {

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

        SettlementCalculatedEvent settlementEvent =
                payment.createSettlementCalculatedEvent(
                        settlementAmountCalculator.calculate(payment.getAmount(), policy)
                );

        sellerSettlementLedgerRepository.saveLedger(
                payment.getSellerId(),
                "EARN",
                settlementEvent.settlementAmount(),
                payment.getId(),
                payment.getIdempotencyKey()
        );

        outboxPort.save(outboxMessageCreator.paymentApproved(payment.createApprovedEvent()));
        outboxPort.save(outboxMessageCreator.settlementCalculated(settlementEvent));

        temporaryPaymentPort.deleteByOrderId(temp.getOrderId());

        return payment;
    }

}

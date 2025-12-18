package com.smore.payment.payment.application;

import com.smore.payment.payment.application.port.in.ApprovePaymentCommand;
import com.smore.payment.payment.application.port.in.ApprovePaymentResult;
import com.smore.payment.payment.application.port.in.ApprovePaymentUseCase;
import com.smore.payment.payment.application.port.out.PaymentRepository;
import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.application.port.out.TemporaryPaymentPort;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovePaymentService implements ApprovePaymentUseCase {

    private final TemporaryPaymentPort temporaryPaymentPort;
    private final PaymentRepository paymentRepository;
    private final PgClient pgClient;
    private final PaymentFinalizeService paymentFinalizeService;
    private final PaymentAuditLogService paymentAuditLogService;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApprovePaymentResult approve(ApprovePaymentCommand command) {

        TemporaryPayment temp = temporaryPaymentPort.findByOrderId(command.orderId())
                .orElseThrow();

        paymentAuditLogService.logApprovalRequested(command, temp);

        temp.validateApproval(command.amount());

        paymentRepository.findByIdempotencyKey(temp.getIdempotencyKey())
                .ifPresent(p -> {
                    throw new IllegalStateException("이미 승인");
                });


        PgResponseResult pgResult = findOrRequestPgApproval(command, temp);

        if (!pgResult.pgStatus().equals("DONE")) {
            return ApprovePaymentResult.failed(
                    command.orderId(),
                    command.amount(),
                    pgResult.failureCode(),
                    pgResult.failureMessage()
            );
        }

        try {
            Payment payment = paymentFinalizeService.finalizePayment(temp, pgResult);
            paymentAuditLogService.logPaymentApprovalSucceeded(payment);

            return ApprovePaymentResult.success(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getApprovedAt(),
                    payment.getStatus().getDesc()
            );

        } catch (RuntimeException e) {
            paymentAuditLogService.logPaymentApprovalFailed(temp, e.getMessage());
            throw e;
        }
    }


    private PgResponseResult findOrRequestPgApproval(ApprovePaymentCommand command, TemporaryPayment temp) {

        if (temp.hasPgApprovalResult()) {
            return temp.getPgResponseResult();
        }

        try {

            paymentAuditLogService.logPgApprovalRequested(command, temp);

            PgResponseResult pgResult = pgClient.approve(
                    command.paymentKey(),
                    command.pgOrderId(),
                    command.amount()
            );

            if (!pgResult.pgStatus().equals("DONE")) {
                paymentAuditLogService.logPgApprovalFailed(
                        temp,
                        pgResult.failureMessage()
                );

                return pgResult;
            }

            temp.setPgResponseResult(pgResult);
            temporaryPaymentPort.update(temp);

            paymentAuditLogService.logPgApprovalSucceeded(temp, pgResult);

            return pgResult;

        } catch (RuntimeException e) {
            paymentAuditLogService.logPgApprovalFailed(temp, e.getMessage());
            throw e;
        }
    }

}


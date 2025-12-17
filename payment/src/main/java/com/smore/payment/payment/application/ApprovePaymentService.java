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

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovePaymentService implements ApprovePaymentUseCase {

    private final TemporaryPaymentPort temporaryPaymentPort;
    private final PaymentRepository paymentRepository;
    private final PgClient pgClient;
    private final PaymentFinalizeService paymentFinalizeService;


    @Override
    public ApprovePaymentResult approve(ApprovePaymentCommand command) {

        TemporaryPayment temp = temporaryPaymentPort.findByOrderId(command.orderId())
                .orElseThrow();

        PgResponseResult pgResult = approveWithPg(command, temp);

        Payment payment = paymentFinalizeService.finalizePayment(temp, pgResult);

        return new ApprovePaymentResult(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getApprovedAt(),
                payment.getStatus().name()
        );
    }

    public PgResponseResult approveWithPg(
            ApprovePaymentCommand command,
            TemporaryPayment temp
    ) {

        temp.validateApproval(command.amount());

        paymentRepository.findByIdempotencyKey(temp.getIdempotencyKey())
                .ifPresent(p -> { throw new IllegalStateException("이미 승인"); });

        return pgClient.approve(
                command.paymentKey(),
                command.pgOrderId(),
                command.amount()
        );
    }
    
}


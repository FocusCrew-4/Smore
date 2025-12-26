package com.smore.payment.payment.application;

import com.smore.payment.payment.application.facade.FeePolicyFacade;
import com.smore.payment.payment.application.facade.dto.FeePolicyResult;
import com.smore.payment.payment.application.port.in.ApprovePaymentCommand;
import com.smore.payment.payment.application.port.in.ApprovePaymentResult;
import com.smore.payment.payment.application.port.in.ApprovePaymentUseCase;
import com.smore.payment.payment.application.port.out.*;
import com.smore.payment.payment.domain.event.SettlementCalculatedEvent;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.service.SettlementAmountCalculator;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovePaymentService implements ApprovePaymentUseCase {

    private final ApiInboxPort apiInboxPort;
    private final TemporaryPaymentPort temporaryPaymentPort;
    private final PaymentRepository paymentRepository;

    private final PaymentAuditLogService paymentAuditLogService;

    private final PaymentFinalizeCreate paymentFinalizeCreate;

    private final PgClient pgClient;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ApprovePaymentResult approve(ApprovePaymentCommand command) {

        // 단기 멱등(redis)
        Optional<ApprovePaymentResult> cached =
                apiInboxPort.find(command.idempotencyKey().toString());

        if (cached.isPresent()) {
            return cached.get();
        }

        // 장기 멱등(rdb) 후 redis 캐싱
        Optional<Payment> existingPayment =
                paymentRepository.findByIdempotencyKey(command.idempotencyKey());

        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

            ApprovePaymentResult result = ApprovePaymentResult.success(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getApprovedAt(),
                    payment.getStatus().getDesc()
            );

            apiInboxPort.save(command.idempotencyKey().toString(), result); // 캐시 재적재
            return result;
        }

        // 결제 승인 시작 - 임시 결제 가져와서 pg에 승인 요청
        TemporaryPayment temp = temporaryPaymentPort.findByOrderId(command.orderId())
                .orElseThrow(() -> new IllegalArgumentException("임시 결제 데이터가 존재하지 않습니다."));

        // 금액 검증
        temp.validateApproval(command.amount());

        // 승인 요청 감사 로그 작성(mongodb)
        paymentAuditLogService.logApprovalRequested(command, temp);

        // pg 결제 승인 요청
        PgResponseResult pgResult = findOrRequestPgApproval(command, temp);

        // pg 결제 실패 시 실패 이유를 반환
        if (!pgResult.pgStatus().equals("DONE")) {
            return ApprovePaymentResult.failed(
                    command.orderId(),
                    command.amount(),
                    pgResult.failureCode(),
                    pgResult.failureMessage()
            );
        }

        ApprovePaymentResult result;

        try {
            // 결제 내역 저장 및 수수료 정책 조회하여 정산금 계산 후 이벤트 발행
            Payment payment = paymentFinalizeCreate.finalizePayment(temp, pgResult);

            // 결제 승인 완료 감사 로그 작성
            paymentAuditLogService.logPaymentApprovalSucceeded(payment);

            result = ApprovePaymentResult.success(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getApprovedAt(),
                    payment.getStatus().getDesc()
            );

        } catch (DataIntegrityViolationException e) {
            // UNIQUE 제약 충돌 → 이미 생성된 Payment
            Payment existing = paymentRepository
                    .findByIdempotencyKey(command.idempotencyKey())
                    .orElseThrow();

            result = ApprovePaymentResult.success(
                    existing.getId(),
                    existing.getOrderId(),
                    existing.getAmount(),
                    existing.getApprovedAt(),
                    existing.getStatus().getDesc()
            );
        } catch (RuntimeException e) {
            paymentAuditLogService.logPaymentApprovalFailed(temp, e.getMessage());
            throw e;
        }

        apiInboxPort.save(command.idempotencyKey().toString(), result);
        return result;
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


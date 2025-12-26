package com.smore.payment.payment.application;

import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.inbound.PaymentRequestedEvent;
import com.smore.payment.payment.application.port.in.ApprovePaymentCommand;
import com.smore.payment.payment.application.port.out.PaymentAuditLogPort;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.infrastructure.persistence.mongo.model.PaymentAuditEventType;
import com.smore.payment.payment.infrastructure.persistence.mongo.model.PaymentAuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentAuditLogService {

    private final PaymentAuditLogPort paymentAuditLogPort;

    public void logTemporaryPaymentCreated(PaymentRequestedEvent event) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.TEMPORARY_PAYMENT_CREATED)
                        .orderId(event.orderId())
                        .userId(event.userId())
                        .sellerId(event.sellerId())
                        .categoryId(event.categoryId())
                        .auctionType(event.auctionType())
                        .amount(event.amount())
                        .idempotencyKey(event.idempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description("임시 결제 생성")
                        .build()
        );
    }

    public void logApprovalRequested(ApprovePaymentCommand command, TemporaryPayment temp) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PAYMENT_APPROVAL_REQUESTED)
                        .orderId(command.orderId())
                        .paymentKey(command.paymentKey())
                        .pgOrderId(command.pgOrderId())
                        .userId(temp.getUserId())
                        .sellerId(temp.getSellerId())
                        .categoryId(temp.getCategoryId())
                        .auctionType(temp.getAuctionType())
                        .amount(command.amount())
                        .idempotencyKey(temp.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description("결제 승인 요청")
                        .build()
        );
    }

    public void logPgApprovalRequested(ApprovePaymentCommand command, TemporaryPayment temp) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PG_APPROVAL_REQUESTED)
                        .orderId(command.orderId())
                        .paymentKey(command.paymentKey())
                        .pgOrderId(command.pgOrderId())
                        .userId(temp.getUserId())
                        .sellerId(temp.getSellerId())
                        .categoryId(temp.getCategoryId())
                        .auctionType(temp.getAuctionType())
                        .amount(command.amount())
                        .idempotencyKey(temp.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description("PG 승인 요청")
                        .build()
        );
    }

    public void logPgApprovalSucceeded(TemporaryPayment temp, PgResponseResult result) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PG_APPROVAL_SUCCEEDED)
                        .orderId(temp.getOrderId())
                        .paymentKey(result.paymentKey())
                        .pgOrderId(result.pgOrderId())
                        .userId(temp.getUserId())
                        .sellerId(temp.getSellerId())
                        .categoryId(temp.getCategoryId())
                        .auctionType(temp.getAuctionType())
                        .amount(result.totalAmount())
                        .idempotencyKey(temp.getIdempotencyKey())
                        .occurredAt(result.approvedAt())
                        .description("PG 승인 완료")
                        .build()
        );
    }

    public void logPgApprovalFailed(TemporaryPayment temp, String reason) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PG_APPROVAL_FAILED)
                        .orderId(temp.getOrderId())
                        .userId(temp.getUserId())
                        .sellerId(temp.getSellerId())
                        .categoryId(temp.getCategoryId())
                        .auctionType(temp.getAuctionType())
                        .amount(temp.getAmount())
                        .idempotencyKey(temp.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description(reason)
                        .build()
        );
    }

    public void logPaymentApprovalSucceeded(Payment payment) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PAYMENT_APPROVAL_SUCCEEDED)
                        .orderId(payment.getOrderId())
                        .paymentId(payment.getId())
                        .paymentKey(payment.getPaymentKey())
                        .pgOrderId(payment.getPgOrderId())
                        .userId(payment.getUserId())
                        .sellerId(payment.getSellerId())
                        .categoryId(payment.getCategoryId())
                        .auctionType(payment.getAuctionType())
                        .amount(payment.getAmount())
                        .idempotencyKey(payment.getIdempotencyKey())
                        .occurredAt(payment.getApprovedAt())
                        .description("결제 승인 완료")
                        .build()
        );
    }

    public void logPaymentApprovalFailed(TemporaryPayment temp, String reason) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PAYMENT_APPROVAL_FAILED)
                        .orderId(temp.getOrderId())
                        .userId(temp.getUserId())
                        .sellerId(temp.getSellerId())
                        .categoryId(temp.getCategoryId())
                        .auctionType(temp.getAuctionType())
                        .amount(temp.getAmount())
                        .idempotencyKey(temp.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description(reason)
                        .build()
        );
    }

    public void logRefundRequested(PaymentRefundEvent event) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PAYMENT_REFUND_REQUESTED)
                        .orderId(event.orderId())
                        .paymentId(event.paymentId())
                        .refundId(event.refundId())
                        .userId(event.userId())
                        .amount(event.refundAmount())
                        .idempotencyKey(event.idempotencyKey())
                        .occurredAt(event.publishedAt())
                        .description(event.refundReason())
                        .build()
        );
    }

    public void logPgRefundRequested(Payment payment, PaymentRefundEvent event, BigDecimal refundAmount) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PG_REFUND_REQUESTED)
                        .orderId(payment.getOrderId())
                        .paymentId(payment.getId())
                        .refundId(event.refundId())
                        .userId(payment.getUserId())
                        .sellerId(payment.getSellerId())
                        .categoryId(payment.getCategoryId())
                        .auctionType(payment.getAuctionType())
                        .amount(refundAmount)
                        .paymentKey(payment.getPaymentKey())
                        .pgOrderId(payment.getPgOrderId())
                        .idempotencyKey(payment.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description(event.refundReason())
                        .build()
        );
    }

    public void logPgRefundSucceeded(Payment payment, PaymentRefundEvent event, PgResponseResult result) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PG_REFUND_SUCCEEDED)
                        .orderId(payment.getOrderId())
                        .paymentId(payment.getId())
                        .refundId(event.refundId())
                        .userId(payment.getUserId())
                        .sellerId(payment.getSellerId())
                        .categoryId(payment.getCategoryId())
                        .auctionType(payment.getAuctionType())
                        .amount(result.cancels().cancelAmount())
                        .paymentKey(payment.getPaymentKey())
                        .pgOrderId(payment.getPgOrderId())
                        .idempotencyKey(payment.getIdempotencyKey())
                        .occurredAt(result.cancels().canceledAt())
                        .description(result.cancels().cancelReason())
                        .build()
        );
    }

    public void logPgRefundFailed(Payment payment, PaymentRefundEvent event, String reason) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PG_REFUND_FAILED)
                        .orderId(payment.getOrderId())
                        .paymentId(payment.getId())
                        .refundId(event.refundId())
                        .userId(payment.getUserId())
                        .sellerId(payment.getSellerId())
                        .categoryId(payment.getCategoryId())
                        .auctionType(payment.getAuctionType())
                        .amount(event.refundAmount())
                        .paymentKey(payment.getPaymentKey())
                        .pgOrderId(payment.getPgOrderId())
                        .idempotencyKey(payment.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description(reason)
                        .build()
        );
    }

    public void logRefundSucceeded(Payment payment, PaymentRefundEvent event) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PAYMENT_REFUND_SUCCEEDED)
                        .orderId(payment.getOrderId())
                        .paymentId(payment.getId())
                        .refundId(event.refundId())
                        .userId(payment.getUserId())
                        .sellerId(payment.getSellerId())
                        .categoryId(payment.getCategoryId())
                        .auctionType(payment.getAuctionType())
                        .amount(event.refundAmount())
                        .idempotencyKey(payment.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description("결제 환불 완료")
                        .build()
        );
    }

    public void logRefundFailed(Payment payment, PaymentRefundEvent event, String reason) {
        paymentAuditLogPort.save(
                PaymentAuditLog.builder()
                        .eventType(PaymentAuditEventType.PAYMENT_REFUND_FAILED)
                        .orderId(payment.getOrderId())
                        .paymentId(payment.getId())
                        .refundId(event.refundId())
                        .userId(payment.getUserId())
                        .sellerId(payment.getSellerId())
                        .categoryId(payment.getCategoryId())
                        .auctionType(payment.getAuctionType())
                        .amount(event.refundAmount())
                        .idempotencyKey(payment.getIdempotencyKey())
                        .occurredAt(LocalDateTime.now())
                        .description(reason)
                        .build()
        );
    }
}
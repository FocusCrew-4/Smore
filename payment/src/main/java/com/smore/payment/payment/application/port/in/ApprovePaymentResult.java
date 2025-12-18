package com.smore.payment.payment.application.port.in;

import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApprovePaymentResult(
        UUID paymentId,
        UUID orderId,
        BigDecimal approvedAmount,
        LocalDateTime approvedAt,
        String status,
        String failureCode,
        String failureMessage
) {

    public static ApprovePaymentResult success(
            UUID paymentId,
            UUID orderId,
            BigDecimal amount,
            LocalDateTime approvedAt,
            String status
    ) {
        return new ApprovePaymentResult(
                paymentId,
                orderId,
                amount,
                approvedAt,
                status,
                null,
                null
        );
    }

    public static ApprovePaymentResult failed(
            UUID orderId,
            BigDecimal amount,
            String failureCode,
            String failureMessage
    ) {
        return new ApprovePaymentResult(
                null,
                orderId,
                amount,
                null,
                "FAILED",
                failureCode,
                failureMessage
        );
    }
}
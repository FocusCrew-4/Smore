package com.smore.payment.payment.domain.service;

import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.facade.dto.CancelPolicyResult;
import com.smore.payment.payment.application.facade.dto.RefundPolicyResult;
import com.smore.payment.payment.domain.model.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RefundCalculator {

    public RefundDecision decide(Payment payment, PaymentRefundEvent event,
                                 CancelPolicyResult cancelResult, RefundPolicyResult refundResult) {

        boolean cancelAvailable = cancelResult.cancellable(payment.getApprovedAt(), event.publishedAt());
        if (cancelAvailable) {
            BigDecimal refundAmount = cancelResult.calculateCancelFee(event.refundAmount());
            return RefundDecision.success(refundAmount);
        }

        if (refundResult == null) {
            return RefundDecision.failure("환불 정책을 찾을 수 없습니다.");
        }

        if (!refundResult.refundable(payment.getApprovedAt(), event.publishedAt())) {
            return RefundDecision.failure("환불이 불가능 한 상품입니다.");
        }

        BigDecimal refundAmount = refundResult.calculateRefundFee(event.refundAmount());
        return RefundDecision.success(refundAmount);
    }
}
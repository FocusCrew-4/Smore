package com.smore.payment.payment.domain.event;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PaymentApprovedEvent {

    private final UUID orderId;
    private final UUID paymentId;
    private final BigDecimal amount;
    private final LocalDateTime approvedAt;

    public PaymentApprovedEvent(UUID orderId, UUID paymentId, BigDecimal amount, LocalDateTime approvedAt) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.approvedAt = approvedAt;
    }

}

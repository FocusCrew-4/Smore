package com.smore.payment.payment.infrastructure.persistence.jpa.model.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public record PaymentFailureJpa(
        @Column(name = "failure_reason")
        String reason,

        @Column(name = "failed_at")
        LocalDateTime failedAt
) {
    public static PaymentFailureJpa of(String reason, LocalDateTime occurredAt) {
        return new PaymentFailureJpa(reason, occurredAt);
    }
}

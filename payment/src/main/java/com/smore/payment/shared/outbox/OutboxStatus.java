package com.smore.payment.shared.outbox;

public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED
}
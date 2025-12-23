package com.smore.payment.payment.infrastructure.persistence.inbox;

public enum RefundInboxStatus {
    RECEIVED,
    PROCESSING,
    POLICY_PASSED,
    PG_REQUESTED,
    PG_SUCCEEDED,
    FINALIZED,
    FAILED
}
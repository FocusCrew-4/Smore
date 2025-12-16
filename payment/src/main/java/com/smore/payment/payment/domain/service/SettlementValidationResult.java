package com.smore.payment.payment.domain.service;

public record SettlementValidationResult(boolean valid, boolean duplicated, String failureReason) {

    public static SettlementValidationResult duplicated() {
        return new SettlementValidationResult(false, true, null);
    }

    public static SettlementValidationResult invalid(String failureReason) {
        return new SettlementValidationResult(false, false, failureReason);
    }

    public static SettlementValidationResult valid() {
        return new SettlementValidationResult(true, false, null);
    }
}
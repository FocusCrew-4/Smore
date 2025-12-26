package com.smore.payment.payment.domain.service;

public record SettlementValidationResult(boolean valid, boolean duplicated, String failureReason) {

}
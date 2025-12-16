package com.smore.payment.policy.refund.domain.model;

public record TargetKeyString(String value) implements TargetKey {
    @Override
    public Object getTargetKey() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value;
    }
}

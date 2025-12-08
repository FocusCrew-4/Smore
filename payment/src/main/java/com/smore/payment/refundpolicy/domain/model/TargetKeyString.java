package com.smore.payment.refundpolicy.domain.model;

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

package com.smore.payment.cancelpolicy.domain.model;

public record TargetKeyLong(Long value) implements TargetKey {
    @Override
    public Object getTargetKey() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value.toString();
    }
}

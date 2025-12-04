package com.smore.payment.feepolicy.domain.model;

import java.util.UUID;

public record TargetKeyUUID(UUID value) implements TargetKey {
    @Override
    public Object getTargetKey() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value.toString();
    }
}

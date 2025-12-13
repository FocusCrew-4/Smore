package com.smore.bidcompetition.domain.status;

public enum InventoryChangeType {

    RESERVE("재고 예약", "RESERVE"),
    EXPIRED("경쟁 만료", "EXPIRED"),
    REFUND("환불", "REFUND")
    ;

    private static final String SEPARATOR = ":";

    private final String description;
    private final String prefix;

    InventoryChangeType(String description, String prefix) {
        this.description = description;
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public String getPrefix() {
        return prefix;
    }

    public String idempotencyKey(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be null or blank");
        }
        return prefix + SEPARATOR + identifier;
    }
}

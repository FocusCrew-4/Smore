package com.smore.seller.application.port.out;

public interface OutboxRepositoryPort {
    void saveOutBox(String topic, Long key, String payload);
}

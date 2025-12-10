package com.smore.seller.application.port.out;

import java.time.Clock;

public interface OutboxRepositoryPort {
    void saveOutBox(String topic, Long key, String payload, Clock clock);
}

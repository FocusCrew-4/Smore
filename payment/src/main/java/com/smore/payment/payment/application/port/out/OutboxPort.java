package com.smore.payment.payment.application.port.out;

import com.smore.payment.shared.outbox.OutboxMessage;

public interface OutboxPort {
    void save(OutboxMessage outboxMessage);
}

package com.smore.seller.application.port.in;

import java.util.UUID;

public interface InboxService {
    void processOnce(UUID idempotencyKey, Runnable function);
}

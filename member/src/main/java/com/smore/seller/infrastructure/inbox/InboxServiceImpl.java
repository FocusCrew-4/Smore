package com.smore.seller.infrastructure.inbox;

import com.smore.seller.application.port.in.InboxService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InboxServiceImpl implements InboxService {

    private final SellerInboxRepository inboxRepository;

    @Override
    @Transactional
    public void processOnce(UUID idempotencyKey, Runnable function) {
        if (inboxRepository.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        function.run();

        inboxRepository.save(
            SellerInbox.success(idempotencyKey)
        );
    }
}

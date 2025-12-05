package com.smore.seller.infrastructure.outbox;

import com.smore.seller.infrastructure.outbox.SellerOutbox.MessageStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 조회전용 계층
@Component
@RequiredArgsConstructor
public class SellerOutboxFetcher {

    private final SellerOutboxRepository sellerOutboxRepository;

    public List<SellerOutbox> fetchPending() {

        //region 동적으로 배치사이즈를 변경하고 싶으면 인자로 int batchSize 를 받아와서 사용
//        Pageable pageable = PageRequest.of(0, batchSize);
//
//        sellerOutboxRepository.findByStatusOrderByIdAsc(
//            MessageStatus.PENDING, pageable
//        );
        //endregion

        return sellerOutboxRepository.findTop100ByStatusOrderByIdAsc(
            MessageStatus.PENDING
        );
    }
}

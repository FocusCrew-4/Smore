package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.BidInventoryLog;

public interface BidInventoryLogRepository {

    BidInventoryLog findByIdempotencyKey(String idempotencyKey);

    BidInventoryLog saveAndFlush(BidInventoryLog log);

}

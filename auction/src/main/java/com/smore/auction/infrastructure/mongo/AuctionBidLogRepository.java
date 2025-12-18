package com.smore.auction.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuctionBidLogRepository
extends MongoRepository<AuctionBidLogDocument, String> {

}

package com.smore.auction.infrastructure.mongo;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auction_bid_log")
@CompoundIndex(
    name = "auction_created_idx",
    def = "{'auctionId': 1, 'createdAt': -1}"
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionBidLogDocument {

    @Id
    private String id;

    private UUID auctionId;
    private Long bidderId;
    private Integer quantity;
    private Double bidPrice;
    @Indexed(expireAfter = "30d")
    private Instant createdAt;
}

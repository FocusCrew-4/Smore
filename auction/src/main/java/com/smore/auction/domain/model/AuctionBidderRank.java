package com.smore.auction.domain.model;

import com.smore.auction.domain.vo.Bidder;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionBidderRank {

    private final UUID id;
    private final Auction auction;
    private Bidder bidder;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

}

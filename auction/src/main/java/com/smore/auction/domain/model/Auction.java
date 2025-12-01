package com.smore.auction.domain.model;

import com.smore.auction.domain.enums.AuctionStatus;
import com.smore.auction.domain.vo.Product;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Auction {

    private final UUID id;
    private Product product;
    private Long stock;
    private Long sellerId;
    private AuctionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;


}

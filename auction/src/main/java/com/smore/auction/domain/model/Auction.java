package com.smore.auction.domain.model;

import com.smore.auction.domain.enums.AuctionStatus;
import com.smore.auction.domain.vo.Product;
import java.math.BigDecimal;
import java.time.Clock;
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

    public static Auction create(
        UUID productId,
        BigDecimal productPrice,
        Long stock,
        Long sellerId,
        Clock clock
    ) {
        LocalDateTime now = LocalDateTime.now(clock);
        return new Auction(
            null,
            new Product(productId, productPrice),
            stock,
            sellerId,
            AuctionStatus.READY,
            now,
            now,
            null,
            null
        );
    }

    public boolean isReady() {
        return this.status == AuctionStatus.READY;
    }

    public void start() {
        this.status = AuctionStatus.OPEN;
    }
}

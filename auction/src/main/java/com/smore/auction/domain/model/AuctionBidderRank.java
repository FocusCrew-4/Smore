package com.smore.auction.domain.model;

import com.smore.auction.domain.enums.BidderStatus;
import com.smore.auction.domain.vo.Bidder;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionBidderRank {

    private final UUID id;
    private final UUID auctionId;
    private Bidder bidder;
    private BidderStatus status;
    private Long rank;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public static AuctionBidderRank createWinner(
        UUID auctionId,
        Long userId,
        BigDecimal price,
        Integer quantity,
        Long rank,
        Clock clock
    ) {
        return new AuctionBidderRank(
            null,
            auctionId,
            new Bidder(userId, price, quantity),
            BidderStatus.WINNER,
            rank,
            LocalDateTime.now(clock),
            LocalDateTime.now(clock),
            null,
            null
        );
    }

    public static AuctionBidderRank createStandBy(
        UUID auctionId,
        Long userId,
        BigDecimal price,
        Integer quantity,
        Long rank,
        Clock clock
    ) {
        return new AuctionBidderRank(
            null,
            auctionId,
            new Bidder(userId, price, quantity),
            BidderStatus.STANDBY,
            rank,
            LocalDateTime.now(clock),
            LocalDateTime.now(clock),
            null,
            null
        );
    }

    public void confirm() {
        this.status = BidderStatus.CONFIRMED;
    }
    public void releaseWinner() {
        this.status = BidderStatus.CANCELLED;
    }
    public void succeedFrom(
        AuctionBidderRank previousWinner
    ) {
        previousWinner.releaseWinner();
        this.status = BidderStatus.WINNER;
    }
}

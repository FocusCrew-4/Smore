package com.smore.auction.infrastructure.persistance.jpa.entity;

import com.smore.auction.infrastructure.persistance.jpa.vo.BidderEmbeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_auction_bidder_rank")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionBidderRankJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private AuctionJpa auction;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "bidder_id")),
        @AttributeOverride(name = "price", column = @Column(name = "bidder_price", precision = 19, scale = 2)),
        @AttributeOverride(name = "quantity", column = @Column(name = "bidder_quantity")),
        @AttributeOverride(name = "status", column = @Column(name = "bidder_status"))
    })
    private BidderEmbeddable bidder;
    private Long rank;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public void belongToAuction(AuctionJpa auction) {
        this.auction = auction;
    }
}

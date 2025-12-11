package com.smore.bidcompetition.infrastructure.persistence.entity;

import com.smore.bidcompetition.domain.status.BidStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_bid_competition")
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidCompetitionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "bid_status", nullable = false)
    private BidStatus bidStatus;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    public static BidCompetitionEntity create(
        UUID productId,
        Long sellerId,
        Integer stock,
        BidStatus bidStatus,
        LocalDateTime startedAt,
        LocalDateTime closedAt
    ) {
        if (productId == null) throw new IllegalArgumentException("상품 식별자는 필수값입니다.");
        if (sellerId == null) throw new IllegalArgumentException("판매자 식별자는 필수값입니다.");
        if (stock == null) throw new IllegalArgumentException("재고는 필수값입니다.");
        if (stock < 1) throw new IllegalArgumentException("재고는 1개 이상이어야 합니다.");
        if (bidStatus == null) throw new IllegalArgumentException("경매 상태는 필수값입니다.");
        if (startedAt == null) throw new IllegalArgumentException("시작 날짜는 필수값입니다.");
        if (closedAt == null) throw new IllegalArgumentException("종료 날짜는 필수값입니다.");

        return BidCompetitionEntity.builder()
            .productId(productId)
            .sellerId(sellerId)
            .stock(stock)
            .bidStatus(bidStatus)
            .startedAt(startedAt)
            .closedAt(closedAt)
            .build();
    }
}

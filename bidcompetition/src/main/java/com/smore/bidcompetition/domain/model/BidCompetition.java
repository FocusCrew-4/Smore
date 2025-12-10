package com.smore.bidcompetition.domain.model;

import com.smore.bidcompetition.domain.status.BidStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidCompetition {

    private UUID id;
    private UUID productId;
    private Long sellerId;
    private Integer stock;
    private BidStatus bidStatus;
    private LocalDateTime startedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public static BidCompetition create(
        UUID productId,
        Long sellerId,
        Integer stock,
        LocalDateTime startedAt,
        LocalDateTime closedAt
    ) {

        if (productId == null) throw new IllegalArgumentException("상품 식별자는 필수값입니다.");
        if (sellerId == null) throw new IllegalArgumentException("판매자 식별자는 필수값입니다.");
        if (stock == null) throw new IllegalArgumentException("재고는 필수값입니다.");
        if (stock < 1) throw new IllegalArgumentException("재고는 1개 이상이어야 합니다.");
        if (startedAt == null) throw new IllegalArgumentException("시작 날짜는 필수값입니다.");
        if (closedAt == null) throw new IllegalArgumentException("종료 날짜는 필수값입니다.");

        return BidCompetition.builder()
            .bidStatus(BidStatus.SCHEDULED)
            .productId(productId)
            .sellerId(sellerId)
            .bidStatus(BidStatus.SCHEDULED)
            .stock(stock)
            .startedAt(startedAt)
            .closedAt(closedAt)
            .build();
    }

    public static BidCompetition of(
        UUID id,
        UUID productId,
        Long sellerId,
        Integer stock,
        BidStatus bidStatus,
        LocalDateTime startedAt,
        LocalDateTime closedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Long deletedBy
    ) {
        return BidCompetition.builder()
            .id(id)
            .productId(productId)
            .sellerId(sellerId)
            .stock(stock)
            .bidStatus(bidStatus)
            .startedAt(startedAt)
            .closedAt(closedAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .build();
    }
}

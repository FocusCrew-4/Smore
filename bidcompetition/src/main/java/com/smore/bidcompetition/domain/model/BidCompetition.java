package com.smore.bidcompetition.domain.model;

import com.smore.bidcompetition.domain.status.BidStatus;
import java.math.BigDecimal;
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
    private UUID categoryId;
    private Long sellerId;
    private BigDecimal productPrice;
    private Integer stock;
    private BidStatus bidStatus;
    private UUID idempotencyKey;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public static BidCompetition create(
        UUID productId,
        UUID categoryId,
        Long sellerId,
        BigDecimal productPrice,
        Integer stock,
        UUID idempotencyKey,
        LocalDateTime startedAt,
        LocalDateTime closedAt
    ) {

        if (productId == null) throw new IllegalArgumentException("상품 식별자는 필수값입니다.");
        if (categoryId == null) throw new IllegalArgumentException("카테고리 식별자는 필수값입니다.");
        if (sellerId == null) throw new IllegalArgumentException("판매자 식별자는 필수값입니다.");
        if (productPrice == null) throw new IllegalArgumentException("상품 가격은 필수값입니다.");
        if (productPrice.compareTo(BigDecimal.ZERO) < 1) throw new IllegalArgumentException("상품 가격은 1원 이상이어야 합니다.");
        if (stock == null) throw new IllegalArgumentException("재고는 필수값입니다.");
        if (stock < 1) throw new IllegalArgumentException("재고는 1개 이상이어야 합니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다.");
        if (startedAt == null) throw new IllegalArgumentException("시작 날짜는 필수값입니다.");
        if (closedAt == null) throw new IllegalArgumentException("종료 날짜는 필수값입니다.");

        return BidCompetition.builder()
            .productId(productId)
            .categoryId(categoryId)
            .sellerId(sellerId)
            .productPrice(productPrice)
            .bidStatus(BidStatus.SCHEDULED)
            .idempotencyKey(idempotencyKey)
            .stock(stock)
            .startAt(startedAt)
            .endAt(closedAt)
            .build();
    }

    public static BidCompetition of(
        UUID id,
        UUID productId,
        UUID categoryId,
        Long sellerId,
        BigDecimal productPrice,
        Integer stock,
        BidStatus bidStatus,
        UUID idempotencyKey,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Long deletedBy
    ) {

        return BidCompetition.builder()
            .id(id)
            .productId(productId)
            .categoryId(categoryId)
            .sellerId(sellerId)
            .productPrice(productPrice)
            .stock(stock)
            .bidStatus(bidStatus)
            .idempotencyKey(idempotencyKey)
            .startAt(startAt)
            .endAt(endAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .build();
    }

    public boolean isExpired(LocalDateTime now) {
        return this.endAt.isBefore(now);
    }

    public boolean isNotActive() {
        return this.bidStatus != BidStatus.ACTIVE;
    }

    public boolean isEnd() {
        return this.bidStatus == BidStatus.END;
    }
}

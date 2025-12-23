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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "p_bid_competition",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_bid_idempotency_key",
        columnNames = {"idempotency_key"}
    )
)
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidCompetitionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "bid_status", nullable = false)
    private BidStatus bidStatus;

    @Column(name = "idempotency_key", nullable = false)
    private UUID idempotencyKey;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    public static BidCompetitionEntity create(
        UUID productId,
        UUID categoryId,
        Long sellerId,
        BigDecimal productPrice,
        Integer totalQuantity,
        Integer stock,
        BidStatus bidStatus,
        UUID idempotencyKey,
        LocalDateTime startAt,
        LocalDateTime endAt
    ) {
        if (productId == null) throw new IllegalArgumentException("상품 식별자는 필수값입니다.");
        if (categoryId == null) throw new IllegalArgumentException("카테고리 식별자는 필수값입니다.");
        if (sellerId == null) throw new IllegalArgumentException("판매자 식별자는 필수값입니다.");
        if (productPrice == null) throw new IllegalArgumentException("상품 가격은 필수값입니다.");
        if (productPrice.compareTo(BigDecimal.ZERO) < 1) throw new IllegalArgumentException("상품 가격은 1원 이상이어야 합니다.");
        if (totalQuantity == null) throw new IllegalArgumentException("총 판매 수량은 필수값입니다.");
        if (totalQuantity < 1) throw new IllegalArgumentException("총 판매 수량은 1개 이상이어야 합니다.");
        if (stock == null) throw new IllegalArgumentException("재고는 필수값입니다.");
        if (stock < 1) throw new IllegalArgumentException("재고는 1개 이상이어야 합니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다.");
        if (bidStatus == null) throw new IllegalArgumentException("경매 상태는 필수값입니다.");
        if (startAt == null) throw new IllegalArgumentException("시작 날짜는 필수값입니다.");
        if (endAt == null) throw new IllegalArgumentException("종료 날짜는 필수값입니다.");

        return BidCompetitionEntity.builder()
            .productId(productId)
            .categoryId(categoryId)
            .sellerId(sellerId)
            .productPrice(productPrice)
            .totalQuantity(totalQuantity)
            .stock(stock)
            .bidStatus(bidStatus)
            .idempotencyKey(idempotencyKey)
            .startAt(startAt)
            .endAt(endAt)
            .build();
    }
}

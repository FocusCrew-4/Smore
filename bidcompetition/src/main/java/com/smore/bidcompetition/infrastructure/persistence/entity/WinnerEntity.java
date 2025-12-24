package com.smore.bidcompetition.infrastructure.persistence.entity;


import com.smore.bidcompetition.domain.status.WinnerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
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
    name = "p_winner",
    uniqueConstraints = {
        @UniqueConstraint(
        name = "uk_winner_allocation_key",
        columnNames = {"allocation_key"}
        ),
        @UniqueConstraint(
            name = "uk_winner_idempotency_key",
            columnNames = {"bid_id", "idempotency_key"}
        )
    }
)
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WinnerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "bid_id", nullable = false)
    private UUID bidId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "allocation_key", nullable = false)
    private UUID allocationKey;

    @Column(name = "idempotency_key", nullable = false)
    private UUID idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "winner_status", nullable = false)
    private WinnerStatus winnerStatus;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt; // 최초 주문 시간

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;  // 최초 주문 시간 + 유효 시간 (비즈니스 유효시간)

    @Version
    private Long version;

    public static WinnerEntity create(
        Long userId,
        UUID bidId,
        UUID productId,
        Integer quantity,
        UUID allocationKey,
        UUID idempotencyKey,
        WinnerStatus winnerStatus,
        LocalDateTime orderedAt,
        LocalDateTime expireAt
    ) {

        if (userId == null) {
            throw new IllegalArgumentException("유저 아이디는 필수값입니다.");
        }
        if (bidId == null) {
            throw new IllegalArgumentException("경쟁 아이디는 필수값입니다.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("상품 식별자는 필수값입니다.");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("수량은 필수값입니다.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        if (allocationKey == null) {
            throw new IllegalArgumentException("할당 키는 필수값입니다.");
        }
        if (idempotencyKey == null) {
            throw new IllegalArgumentException("idempotencyKey는 필수값입니다.");
        }
        if (winnerStatus == null) {
            throw new IllegalArgumentException("경매 상태는 필수값입니다.");
        }
        if (orderedAt == null) {
            throw new IllegalArgumentException("주문 시간은 필수값입니다.");
        }
        if (expireAt == null) {
            throw new IllegalArgumentException("유효 시간은 필수값입니다.");
        }
        if (expireAt.isBefore(orderedAt)) {
            throw new IllegalArgumentException("유효 시간은 주문 시간 이후여야 합니다.");
        }

        return WinnerEntity.builder()
            .userId(userId)
            .bidId(bidId)
            .productId(productId)
            .quantity(quantity)
            .allocationKey(allocationKey)
            .idempotencyKey(idempotencyKey)
            .winnerStatus(winnerStatus)
            .orderedAt(orderedAt)
            .expireAt(expireAt)
            .build();
    }
}

package com.smore.bidcompetition.domain.model;

import com.smore.bidcompetition.domain.status.WinnerStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Winner {

    private UUID id;
    private Long userId;
    private UUID bidId;
    private UUID orderId;
    private UUID productId;
    private Integer quantity;
    private UUID allocationKey;
    private UUID idempotencyKey;
    private WinnerStatus winnerStatus;
    private LocalDateTime orderedAt; // 최초 주문 시간
    private LocalDateTime expireAt;  // 최초 주문 시간 + 유효 시간 (비즈니스 유효시간)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
    private Long version;

    public static Winner create(
        Long userId,
        UUID bidId,
        UUID productId,
        Integer quantity,
        UUID allocationKey,
        UUID idempotencyKey,
        LocalDateTime orderedAt,
        LocalDateTime expireAt
    ) {
        if (userId == null) throw new IllegalArgumentException("유저 아이디는 필수값입니다.");
        if (bidId == null) throw new IllegalArgumentException("경쟁 아이디는 필수값입니다.");
        if (productId == null) throw new IllegalArgumentException("상품 식별자는 필수값입니다.");
        if (quantity == null) throw new IllegalArgumentException("수량은 필수값입니다.");
        if (quantity < 1) throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        if (allocationKey == null) throw new IllegalArgumentException("할당 키는 필수값입니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다.");
        if (orderedAt == null) throw new IllegalArgumentException("주문 시간은 필수값입니다.");
        if (expireAt == null) throw new IllegalArgumentException("유효 시간은 필수값입니다.");
        if (expireAt.isBefore(orderedAt)) throw new IllegalArgumentException("유효 시간은 주문 시간 이후여야 합니다.");

        return Winner.builder()
            .userId(userId)
            .bidId(bidId)
            .productId(productId)
            .quantity(quantity)
            .allocationKey(allocationKey)
            .idempotencyKey(idempotencyKey)
            .winnerStatus(WinnerStatus.PAYMENT_PENDING)
            .orderedAt(orderedAt)
            .expireAt(expireAt)
            .build();
    }

    public static Winner of(
        UUID id,
        Long userId,
        UUID bidId,
        UUID orderId,
        UUID productId,
        Integer quantity,
        UUID allocationKey,
        UUID idempotencyKey,
        WinnerStatus winnerStatus,
        LocalDateTime orderedAt,
        LocalDateTime expireAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Long deletedBy,
        Long version
    ) {
        return Winner.builder()
            .id(id)
            .userId(userId)
            .bidId(bidId)
            .orderId(orderId)
            .productId(productId)
            .quantity(quantity)
            .allocationKey(allocationKey)
            .idempotencyKey(idempotencyKey)
            .winnerStatus(winnerStatus)
            .orderedAt(orderedAt)
            .expireAt(expireAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .version(version)
            .build();
    }

    public boolean isPaid() {
        return this.winnerStatus == WinnerStatus.PAID;
    }

    public boolean isExpired() {
        return this.winnerStatus == WinnerStatus.EXPIRED;
    }

    public boolean isCompleted() {
        return this.winnerStatus != WinnerStatus.PAYMENT_PENDING;
    }

}

package com.smore.bidcompetition.infrastructure.persistence.entity;

import com.smore.bidcompetition.domain.status.InventoryChangeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    name = "p_bid_inventory_log",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_bid_inventory_log_idempotency_key",
        columnNames = {"bid_id", "idempotency_key"}
    )
)
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidInventoryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bid_id", nullable = false, updatable = false)
    private UUID bidId;

    @Column(name = "winner_id", nullable = false, updatable = false)
    private UUID winnerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_change_type", nullable = false, updatable = false)
    private InventoryChangeType changeType;

    @Column(name = "stock_before", nullable = false, updatable = false)
    private Integer stockBefore;

    @Column(name = "stock_after", nullable = false, updatable = false)
    private Integer stockAfter;

    @Column(name = "quantity_delta", nullable = false, updatable = false)
    private Integer quantityDelta;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static BidInventoryLogEntity create(
        UUID bidId,
        UUID winnerId,
        InventoryChangeType changeType,
        Integer stockBefore,
        Integer stockAfter,
        Integer quantityDelta,
        String idempotencyKey,
        LocalDateTime createdAt
    ) {
        return BidInventoryLogEntity.builder()
            .bidId(bidId)
            .winnerId(winnerId)
            .changeType(changeType)
            .stockBefore(stockBefore)
            .stockAfter(stockAfter)
            .quantityDelta(quantityDelta)
            .idempotencyKey(idempotencyKey)
            .createdAt(createdAt)
            .build();
    }
}

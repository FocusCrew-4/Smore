package com.smore.bidcompetition.domain.model;

import com.smore.bidcompetition.domain.status.InventoryChangeType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidInventoryLog {
    private UUID id;
    private UUID bidId;
    private UUID winnerId;
    private InventoryChangeType changeType;
    private Integer stockBefore;
    private Integer stockAfter;
    private Integer quantityDelta;
    private String idempotencyKey;
    private LocalDateTime createdAt;

    public static BidInventoryLog create(
        UUID bidId,
        UUID winnerId,
        InventoryChangeType changeType,
        Integer stockBefore,
        Integer stockAfter,
        Integer quantityDelta,
        String idempotencyKey,
        LocalDateTime createdAt
    ) {
        if (bidId == null) throw new IllegalArgumentException("경쟁 아이디는 필수값입니다");
        if (winnerId == null) throw new IllegalArgumentException("경쟁 승리 식별자는 필수값입니다");
        if (changeType == null) throw new IllegalArgumentException("재고 변경 타입은 필수값입니다");
        if (stockBefore == null) throw new IllegalArgumentException("재고 변경 전 수량은 필수값입니다");

        if (quantityDelta == null) throw new IllegalArgumentException("재고 변경 수량은 필수값입니다");
        if (stockAfter == null) throw new IllegalArgumentException("재고 변경 후 수량은 필수값입니다");

        int expectedAfter = stockBefore.intValue() + quantityDelta.intValue();
        if (stockAfter.intValue() != expectedAfter) throw new IllegalArgumentException("재고 변경 후 수량은 [재고 변경 전 수량 + 변경 수량]과 일치해야 합니다");
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new IllegalArgumentException("idempotencyKey는 필수값입니다");
        if (createdAt == null) throw new IllegalArgumentException("생성 시간은 필수값입니다");

        return BidInventoryLog.builder()
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

    public static BidInventoryLog of(
        UUID id,
        UUID bidId,
        UUID winnerId,
        InventoryChangeType changeType,
        Integer stockBefore,
        Integer stockAfter,
        Integer quantityDelta,
        String idempotencyKey,
        LocalDateTime createdAt
    ) {
        if (id == null) throw new IllegalArgumentException("재고 로그 아이디는 필수값입니다");
        if (bidId == null) throw new IllegalArgumentException("경쟁 아이디는 필수값입니다");
        if (winnerId == null) throw new IllegalArgumentException("경쟁 승리 식별자는 필수값입니다");
        if (changeType == null) throw new IllegalArgumentException("재고 변경 타입은 필수값입니다");
        if (stockBefore == null) throw new IllegalArgumentException("재고 변경 전 수량은 필수값입니다");
        if (stockAfter == null) throw new IllegalArgumentException("재고 변경 후 수량은 필수값입니다");
        if (quantityDelta == null) throw new IllegalArgumentException("재고 변경 수량은 필수값입니다");
        if (idempotencyKey == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다");
        if (createdAt == null) throw new IllegalArgumentException("생성 시간은 필수값입니다");

        return BidInventoryLog.builder()
            .id(id)
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

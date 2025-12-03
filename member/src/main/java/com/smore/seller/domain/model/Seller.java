package com.smore.seller.domain.model;

import com.smore.seller.domain.enums.SellerStatus;
import com.smore.seller.domain.vo.Money;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: 정산금액 관리 필요
@Getter
@AllArgsConstructor
public class Seller {
    private final UUID id;
    private final Long memberId;
    private String accountNum;
    private SellerStatus status;
    private Money money;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    public static Seller apply(
        Long memberId,
        String accountNum,
        Clock clock
    ) {
        LocalDateTime now = LocalDateTime.now(clock);
        return new Seller(
            null,
            memberId,
            accountNum,
            SellerStatus.PENDING,
            Money.of(0),
            now,
            now,
            null,
            null
        );
    }
}
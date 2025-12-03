package com.smore.seller.domain.model;

import com.smore.seller.domain.enums.SellerStatus;
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

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;
}
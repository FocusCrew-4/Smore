package com.smore.seller.application.result;

import com.smore.seller.domain.enums.SellerStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SellerResult(
    UUID id,
    Long memberId,
    String accountNum,
    SellerStatus status,
    BigDecimal money,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    Long deletedBy
) {

}

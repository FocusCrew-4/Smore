package com.smore.payment.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryPayment {

    private UUID idempotencyKey;
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private Long sellerId;
    private UUID categoryId;
    private String auctionType;
    private LocalDateTime expiredAt;

    public static TemporaryPayment create(
            UUID idempotencyKey,
            UUID orderId,
            Long userId,
            BigDecimal amount,
            Long sellerId,
            UUID categoryId,
            String auctionType,
            LocalDateTime expiredAt
    ) {
        return new TemporaryPayment(
                idempotencyKey,
                orderId,
                userId,
                amount,
                sellerId,
                categoryId,
                auctionType,
                expiredAt
        );
    }

    public void validateApproval(BigDecimal requestAmount) {
        if (requestAmount == null) {
            throw new IllegalArgumentException("요청 금액이 null입니다.");
        }

        if (this.amount.compareTo(requestAmount) != 0) {
            throw new IllegalArgumentException(
                    String.format("결제 금액이 일치하지 않습니다. 예상: %s, 요청: %s",
                            this.amount, requestAmount)
            );
        }

        if (LocalDateTime.now().isAfter(expiredAt)) {
            throw new IllegalStateException("결제 승인 시간이 만료되었습니다.");
        }
    }
}
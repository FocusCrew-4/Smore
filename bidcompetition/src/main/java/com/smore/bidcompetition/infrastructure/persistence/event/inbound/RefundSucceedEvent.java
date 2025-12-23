package com.smore.bidcompetition.infrastructure.persistence.event.inbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefundSucceedEvent {
    private UUID orderId;
    private UUID refundId;
    private Long userId;
    private Integer quantity;
    private UUID allocationKey;
    private Integer refundAmount;
    private String status;
    private LocalDateTime publishedAt;
}

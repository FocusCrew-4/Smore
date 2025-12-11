package com.smore.bidcompetition.infrastructure.persistence.event.inbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCompletedEvent {

    private UUID orderId;
    private Long userId;
    private String currentOrderStatus;
    private UUID idempotencyKey;
    private LocalDateTime paidAt;

}

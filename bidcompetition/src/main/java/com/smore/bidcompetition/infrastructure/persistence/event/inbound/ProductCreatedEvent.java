package com.smore.bidcompetition.infrastructure.persistence.event.inbound;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreatedEvent {

    private UUID productId;
    private UUID categoryId;
    private Long sellerId;
    private BigDecimal productPrice;
    private Integer stock;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private UUID idempotencyKey;

}

package com.smore.bidcompetition.infrastructure.persistence.event.inbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderFailedEvent {

    private UUID allocationKey;
    private UUID productId;
    private Long userId;
    private Integer quantity;
    private LocalDateTime publishedAt;

}

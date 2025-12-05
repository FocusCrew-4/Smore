package com.smore.order.domain.event;

import com.smore.order.domain.status.RefundStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompletedRefundEvent {
    private UUID orderId;
    private UUID refundId;
    private Integer refundAmount;

}

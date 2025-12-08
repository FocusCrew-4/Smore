package com.smore.order.application.event.inbound;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BidWinnerConfirmedEvent {

    private Long userId;
    private UUID productId;
    private Integer productPrice;
    private Integer quantity;
    private UUID idempotencyKey;
    private LocalDateTime expiresAt;
    private String street;
    private String city;
    private String zipcode;

}

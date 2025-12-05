package com.smore.seller.domain.events;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerRegisterV1Event {
    Long memberId;
    UUID idempotencyKey;
    LocalDateTime dateTime;

    public static SellerRegisterV1Event create(
        Long memberId,
        UUID idempotencyKey,
        Clock clock) {

        return new SellerRegisterV1Event(
            memberId,
            idempotencyKey,
            LocalDateTime.now(clock)
        );
    }
}

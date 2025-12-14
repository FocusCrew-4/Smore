package com.smore.seller.infrastructure.inbox;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID idempotencyKey;

    public static SellerInbox success(
        UUID idempotencyKey
    ) {
        return new SellerInbox(
            null,
            idempotencyKey
        );
    }
}

package com.smore.seller.infrastructure.inbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
    name = "p_seller_inbox",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_p_seller_inbox_idempotency_key",
            columnNames = {"idempotency_key"}
        )
    }
)
public class SellerInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
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

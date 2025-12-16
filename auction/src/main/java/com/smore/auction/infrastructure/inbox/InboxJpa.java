package com.smore.auction.infrastructure.inbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "p_auction_inbox",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_p_auction_inbox",
            columnNames = {"idempotency_key"}
        )
    }
)
public class InboxJpa {

    @Id
    @Column(unique = true)
    private UUID idempotencyKey;
}

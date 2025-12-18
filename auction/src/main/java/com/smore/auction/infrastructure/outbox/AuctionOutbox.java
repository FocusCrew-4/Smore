package com.smore.auction.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "p_auction_outbox")
public class AuctionOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private Long memberId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    private int retryCount;

    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public enum MessageStatus {
        PENDING,
        SENT,
        FAILED
    }

    public AuctionOutbox(
        String eventType,
        Long memberId,
        String payload,
        Clock clock
    ) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.payload = payload;
        this.status = MessageStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now(clock);
    }
}

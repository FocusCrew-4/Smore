package com.smore.product.domain.stock.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_stock_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockLog {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(columnDefinition = "UUID", nullable = false)
    private UUID productId;

    private int beforeStock;
    private int afterStock;

    private LocalDateTime createdAt;
}
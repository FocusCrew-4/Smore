package com.smore.auction.infrastructure.persistance.jpa.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEmbeddable {
    private UUID id;
    @Column(precision = 19, scale = 2)
    private BigDecimal price;
}

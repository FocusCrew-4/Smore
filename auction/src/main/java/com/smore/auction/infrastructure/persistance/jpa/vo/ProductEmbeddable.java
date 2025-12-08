package com.smore.auction.infrastructure.persistance.jpa.vo;

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
    private BigDecimal price;
}

package com.smore.auction.infrastructure.persistance.jpa.vo;

import com.smore.auction.domain.enums.BidderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidderEmbeddable {
    private Long id;
    @Column(precision = 19, scale = 2)
    private BigDecimal price;
    private Integer quantity;
}

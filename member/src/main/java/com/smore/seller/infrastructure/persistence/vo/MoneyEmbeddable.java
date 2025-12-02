package com.smore.seller.infrastructure.persistence.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class MoneyEmbeddable {
    @Column(precision=19, scale=2)
    private BigDecimal amount;
}

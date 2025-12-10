package com.smore.auction.infrastructure.persistance.jpa.vo;

import com.smore.auction.domain.enums.BidderStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Long price;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private BidderStatus status;
}

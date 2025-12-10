package com.smore.auction.domain.vo;

import java.math.BigDecimal;
import java.util.UUID;

public record Product(
    UUID id,
    BigDecimal price
) {

}

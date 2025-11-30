package com.smore.auction.domain.vo;

import java.util.UUID;

public record Product(
    UUID id,
    Long price
) {

}

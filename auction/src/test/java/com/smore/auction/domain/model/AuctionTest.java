package com.smore.auction.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuctionTest {

    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    @DisplayName("Id 와 삭제 관련 필드는 null 이여야 한다")
    void create() {
        // given
        UUID productIdFix = UUID.randomUUID();
        UUID productCategoryIdFix = UUID.randomUUID();

        // when
        var test = Auction.create(
            productIdFix,
            productCategoryIdFix,
            BigDecimal.valueOf(1000L),
            10L,
            1L,
            clock
        );

        // then
        assertAll(
            () -> assertNull(test.getId()),
            () -> assertEquals(productIdFix, test.getProduct().id()),
            () -> assertEquals(productCategoryIdFix, test.getProduct().categoryId()),
            () -> assertEquals(10L, test.getStock()),
            () -> assertEquals(1L, test.getSellerId()),
            () -> assertEquals(BigDecimal.valueOf(1000L), test.getProduct().price())
        );
    }
}

package com.smore.auction.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.smore.auction.config.TestClockConfig;
import com.smore.auction.config.TestContainerConfig;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

class AuctionTest {

    Clock clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    @DisplayName("Id 와 삭제 관련 필드는 null 이여야 한다")
    void create() {
        // given
        UUID productIdFix = UUID.randomUUID();

        // when
        var test = Auction.create(
            productIdFix,
            1000L,
            10L,
            1L,
            clock
        );

        // then
        assertAll(
            () -> assertNull(test.getId()),
            () -> assertEquals(productIdFix, test.getProduct().id()),
            () -> assertEquals(10L, test.getStock()),
            () -> assertEquals(1L, test.getSellerId()),
            () -> assertEquals(1000L, test.getProduct().price())
        );
    }
}
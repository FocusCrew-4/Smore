package com.smore.seller.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.smore.seller.domain.enums.SellerStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class SellerTest {

    @Test
    void apply() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
        Clock clock = Clock.fixed(fixedTime.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

        Seller seller = Seller.apply(1L, "123-45-67890", clock);

        assertAll(
            () -> assertNull(seller.getId()),
            () -> assertEquals(1L, seller.getMemberId()),
            () -> assertEquals("123-45-67890", seller.getAccountNum()),
            () -> assertEquals(SellerStatus.PENDING, seller.getStatus()),
            () -> assertEquals(BigDecimal.ZERO.setScale(2), seller.getMoney().amount()),
            () -> assertEquals(fixedTime, seller.getCreatedAt()),
            () -> assertEquals(fixedTime, seller.getUpdatedAt()),
            () -> assertNull(seller.getDeletedAt()),
            () -> assertNull(seller.getDeletedBy())
        );
    }
}

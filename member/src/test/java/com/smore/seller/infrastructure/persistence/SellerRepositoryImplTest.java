//package com.smore.seller.infrastructure.persistence;
//
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//import com.smore.config.TestContainerConfig;
//import com.smore.member.infrastructure.persistence.config.QuerydslConfig;
//import com.smore.seller.domain.enums.SellerStatus;
//import com.smore.seller.domain.model.Seller;
//import com.smore.seller.domain.repository.SellerRepository;
//import com.smore.seller.domain.vo.Money;
//import com.smore.seller.infrastructure.persistence.jpa.mapper.SellerJpaMapperImpl;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//
//@DataJpaTest
//@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import({
//    QuerydslConfig.class,
//    SellerJpaMapperImpl.class,
//    SellerRepositoryImpl.class,
//    TestContainerConfig.class
//})
//class SellerRepositoryImplTest {
//
//    @Autowired
//    private SellerRepository sellerRepository;
//
//    @Test
//    void saveAndFindByMemberId_shouldPersistAndMapFields() {
//        LocalDateTime now = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
//        Money money = Money.of(new BigDecimal("12345.67"));
//
//        Seller newSeller = new Seller(
//            null,
//            99L,
//            "987-65-43210",
//            SellerStatus.PENDING,
//            money,
//            now,
//            now,
//            null,
//            null
//        );
//
//        Seller saved = sellerRepository.save(newSeller);
//        Seller found = sellerRepository.findByMemberId(99L);
//
//        assertAll(
//            () -> assertNotNull(saved.getId()),
//            () -> assertEquals(saved.getId(), found.getId()),
//            () -> assertEquals(99L, found.getMemberId()),
//            () -> assertEquals("987-65-43210", found.getAccountNum()),
//            () -> assertEquals(SellerStatus.PENDING, found.getStatus()),
//            () -> assertEquals(new BigDecimal("12345.67"), found.getMoney().amount()),
//            () -> assertEquals(now, found.getCreatedAt()),
//            () -> assertEquals(now, found.getUpdatedAt()),
//            () -> assertNull(found.getDeletedAt()),
//            () -> assertNull(found.getDeletedBy())
//        );
//    }
//}

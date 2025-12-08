package com.smore.order.infrastructure.persistence.repository.order;

import static org.junit.jupiter.api.Assertions.*;


import com.smore.order.application.repository.OrderRepository;
import com.smore.order.domain.model.Order;
import com.smore.order.infrastructure.config.JpaConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(
    {
        JpaConfig.class,
        OrderRepositoryImpl.class
    }
)
@ActiveProfiles("test")
class OrderRepositoryImplTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

    @DisplayName("주문 생성을 등록하고 멱등키를 활용하여 주문을 조회할 수 있다.")
    @Test
    void save_and_findByIdempotencyKeyTest() {
        // given
        Long userId = 1L;
        UUID productId = UUID.randomUUID();
        int productPrice = 10_000;
        int quantity = 2;
        UUID idempotencyKey = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2025, 11, 30, 12, 0, 0);
        String street = "서울시 강남구 테헤란로 1";
        String city = "서울시";
        String zipcode = "06234";

        Order order = Order.create(
            userId,
            productId,
            productPrice,
            quantity,
            idempotencyKey,
            now,
            street,
            city,
            zipcode
        );

        // when
        orderRepository.save(order);

        em.flush();
        em.clear();

        Order findOrder = orderRepository.findByIdempotencyKey(idempotencyKey);
        // then

        Assertions.assertThat(findOrder).isNotNull();
        Assertions.assertThat(findOrder.getId()).isNotNull(); // UUID 자동 생성됐는지 확인

        Assertions.assertThat(findOrder.getUserId()).isEqualTo(userId);

        Assertions.assertThat(findOrder.getProduct()).isNotNull();
        Assertions.assertThat(findOrder.getProduct().productId()).isEqualTo(productId);
        Assertions.assertThat(findOrder.getProduct().productPrice()).isEqualTo(productPrice);

        Assertions.assertThat(findOrder.getQuantity()).isEqualTo(quantity);

        Assertions.assertThat(findOrder.getIdempotencyKey()).isEqualTo(idempotencyKey);
        Assertions.assertThat(findOrder.getOrderedAt()).isEqualTo(now);

        Assertions.assertThat(findOrder.getAddress()).isNotNull();
        Assertions.assertThat(findOrder.getAddress().street()).isEqualTo(street);
        Assertions.assertThat(findOrder.getAddress().city()).isEqualTo(city);
        Assertions.assertThat(findOrder.getAddress().zipcode()).isEqualTo(zipcode);

        Assertions.assertThat(findOrder.getRefundReservedQuantity()).isZero();
        Assertions.assertThat(findOrder.getRefundedQuantity()).isZero();
        Assertions.assertThat(findOrder.getRefundedAmount()).isZero();
        Assertions.assertThat(findOrder.getFeeAmount()).isZero();
        Assertions.assertThat(findOrder.getConfirmedAt()).isNull();
        Assertions.assertThat(findOrder.getCancelledAt()).isNull();

    }

}
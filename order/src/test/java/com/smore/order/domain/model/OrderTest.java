package com.smore.order.domain.model;

import com.smore.order.domain.status.CancelState;
import com.smore.order.domain.status.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderTest {

    @DisplayName("주문을 생성할 때 userId가 null이라면 IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @Test
    void createOrderFailWhenUserIdIsNull() {
        // given
        Long userId = null;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("유저 식별자는 필수값입니다.");
    }

    @DisplayName("주문을 생성할 때 productId가 null이라면 IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @Test
    void createOrderFailWhenProductIdIsNull() {
        // given
        Long userId = 1L;

        UUID productId = null;
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("productId는 필수값입니다.");
    }

    @DisplayName("주문을 생성할 때 productId가 null이라면 IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @Test
    void createOrderFailWhenProductPriceIsNull() {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = null;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 가격은 필수값입니다.");
    }

    @DisplayName("주문을 생성할 때 productPrice가 음수일 수 없습니다. IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @Test
    void createOrderFailWhenProductPriceIsNegativeNumber() {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = -1;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 가격은 음수일 수 없습니다.");
    }

    @DisplayName("주문을 생성할 때 주소의 street 값이 null이거나 blank인 경우 IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void createOrderFailWhenStreetIsNull(String street) {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("도로 명 주소 필수 입력");
    }

    @DisplayName("주문을 생성할 때 주소의 city 값이 null이거나 blank인 경우 IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void createOrderFailWhenCityIsNull(String city) {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지번 주소 필수 입력");
    }

    @DisplayName("주문을 생성할 때 주소의 city 값이 null이거나 blank인 경우 IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @NullSource
    void createOrderFailWhenZipcodeIsNull(String zipcode) {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("우편 번호 필수 입력");
    }

    @DisplayName("주문을 생성할 때 주문 수량이 null인 경우  IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @ParameterizedTest
    @NullSource
    void createOrderFailWhenQuantityIsNull(Integer quantity) {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 수량은 필수값입니다.");
    }

    @DisplayName("주문을 생성할 때 주문 수량이 0 이하인 경우 IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void createOrderFailInvalidQuantity(Integer quantity) {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 수량은 1개 이상이어야 합니다.");
    }

    @DisplayName("주문을 생성할 때 멱등키가 null이라면  IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @Test
    void createOrderFailWhenIdempotencyKeyIsNull() {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = null;
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("멱등키는 필수입니다.");
    }

    @DisplayName("주문을 생성할 때 멱등키가 null이라면  IllegalArgumentException 예외가 발생하며 주문 도메인 생성에 실패한다.")
    @Test
    void createOrderFailWhenNowIsNull() {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = null;

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
        Assertions.assertThatThrownBy(() -> Order.create(
                userId,
                productId,
                productPrice,
                quantity,
                idempotencyKey,
                now,
                street,
                city,
                zipcode
            ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("현재 날짜는 필수입니다.");
    }

    @DisplayName("주문이 정상적으로 생성되었는지 테스트")
    @Test
    void createOrder() {
        // given
        Long userId = 1L;

        UUID productId = UUID.fromString("c5d7e9a9-2e86-4947-9a12-2dd6a0b980da");
        Integer productPrice = 15000;

        Integer quantity = 10;

        UUID idempotencyKey = UUID.fromString("b3fb0b90-4a22-49e9-a3b3-8dc98d85e2b1");
        LocalDateTime now = LocalDateTime.of(2025, 11, 29, 12, 0, 0);

        String street = "테헤란로 152";
        String city = "서울특별시 강남구";
        String zipcode = "06236";

        // when // then
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

        // then
        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.getUserId()).isEqualTo(userId);

        Assertions.assertThat(order.getProduct()).isNotNull();
        Assertions.assertThat(order.getProduct().productId()).isEqualTo(productId);
        Assertions.assertThat(order.getProduct().productPrice()).isEqualTo(productPrice);

        Assertions.assertThat(order.getQuantity()).isEqualTo(quantity);
        Assertions.assertThat(order.getTotalAmount()).isEqualTo(productPrice * quantity);

        Assertions.assertThat(order.getIdempotencyKey()).isEqualTo(idempotencyKey);
        Assertions.assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        Assertions.assertThat(order.getCancelState()).isEqualTo(CancelState.NONE);

        Assertions.assertThat(order.getOrderedAt()).isEqualTo(now);

        Assertions.assertThat(order.getAddress()).isNotNull();
        Assertions.assertThat(order.getAddress().street()).isEqualTo(street);
        Assertions.assertThat(order.getAddress().city()).isEqualTo(city);
        Assertions.assertThat(order.getAddress().zipcode()).isEqualTo(zipcode);

        Assertions.assertThat(order.getId()).isNull();
        Assertions.assertThat(order.getRefundReservedQuantity()).isNull();
        Assertions.assertThat(order.getRefundedQuantity()).isNull();
        Assertions.assertThat(order.getRefundedAmount()).isNull();
        Assertions.assertThat(order.getFeeAmount()).isNull();
        Assertions.assertThat(order.getConfirmedAt()).isNull();
        Assertions.assertThat(order.getCancelledAt()).isNull();

    }

}
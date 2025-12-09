package com.smore.order.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smore.order.application.dto.CreateOrderCommand;
import com.smore.order.application.repository.OrderRepository;
import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.domain.model.Order;
import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.AggregateType;
import com.smore.order.domain.status.EventType;
import com.smore.order.domain.status.SaleType;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OutboxRepository outboxRepository;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Mock
    Clock clock;

    @InjectMocks
    OrderService orderService;

    @DisplayName("만약 이미 등록된 주문이라면 createOrder는 즉시 리턴되고 주문을 생성하지 않는다.")
    @Test
    void createOrderIdempotencyKeyTest() {
        // given
        CreateOrderCommand command = CreateOrderCommand.create(
            1L,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            10000,
            2,
            UUID.fromString("9f3c3a4c-8b6c-4f9a-9f4f-0e4c62e2b3c7"),
            SaleType.BID,
            1L,
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            LocalDateTime.of(2025, 11, 30, 12, 0, 0),
            "서울시 강남구 테헤란로 1",
            "서울시",
            "06234"
        );

        Order order = Order.create(
            1L,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            10_000,
            2,
            UUID.fromString("9f3c3a4c-8b6c-4f9a-9f4f-0e4c62e2b3c7"),
            SaleType.BID,
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            LocalDateTime.of(2025, 11, 30, 12, 0, 0),
            "서울시 강남구 테헤란로 1",
            "서울시",
            "06234"
        );

        Mockito.when(orderRepository.findByIdempotencyKey(command.getIdempotencyKey()))
            .thenReturn(order);

        // when
        orderService.createOrder(command);

        // then
        Mockito.verify(
            orderRepository, Mockito.times(1)
        ).findByIdempotencyKey(command.getIdempotencyKey());

        Mockito.verify(
            orderRepository, Mockito.times(0)
        ).save(Mockito.any());

        Mockito.verify(
            outboxRepository, Mockito.times(0)
        ).save(Mockito.any());
    }

    @DisplayName("createOrder 성공 케이스 테스트 코드")
    @Test
    void createOrder() {
        // given
        Instant fixedInstant = Instant.parse("2025-10-11T00:00:00Z");

        Mockito.when(clock.instant()).thenReturn(fixedInstant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        CreateOrderCommand command = CreateOrderCommand.create(
            1L,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            10000,
            2,
            UUID.fromString("9f3c3a4c-8b6c-4f9a-9f4f-0e4c62e2b3c7"),
            SaleType.BID,
            1L,
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            LocalDateTime.of(2025, 11, 30, 12, 0, 0),
            "서울시 강남구 테헤란로 1",
            "서울시",
            "06234"
        );

        Order order = Order.create(
            1L,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            10_000,
            2,
            UUID.fromString("9f3c3a4c-8b6c-4f9a-9f4f-0e4c62e2b3c7"),
            SaleType.BID,
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            LocalDateTime.of(2025, 11, 30, 12, 0, 0),
            "서울시 강남구 테헤란로 1",
            "서울시",
            "06234"
        );

        ReflectionTestUtils.setField(order, "id",
            UUID.fromString("3f9b8c1d-4e7e-4c1a-a8fd-2a6a7f9c3b44"));

        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            order.getId(),
            EventType.ORDER_CREATED,
            UUID.randomUUID(),
            "payload"
        );

        ReflectionTestUtils.setField(outbox, "id",
            1L);

        Mockito.when(orderRepository.findByIdempotencyKey(command.getIdempotencyKey()))
            .thenReturn(null);

        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
            .thenReturn(order);

        Mockito.when(outboxRepository.save(Mockito.any(Outbox.class)))
            .thenReturn(outbox);

        // when
        orderService.createOrder(command);

        // then
        Mockito.verify(
            orderRepository, Mockito.times(1)
        ).findByIdempotencyKey(command.getIdempotencyKey());

        Mockito.verify(
            orderRepository, Mockito.times(1)
        ).save(Mockito.any());

        Mockito.verify(
            outboxRepository, Mockito.times(1)
        ).save(Mockito.any());
    }


}
package com.smore.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.order.application.dto.CreateOrderCommand;
import com.smore.order.application.repository.OrderRepository;
import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.domain.event.CreatedOrderEvent;
import com.smore.order.domain.model.Order;
import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.AggregateType;
import com.smore.order.domain.status.EventType;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j(topic = "OrderService")
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional
    public void createOrder(CreateOrderCommand command) {

        Order findOrder = orderRepository.findByIdempotencyKey(command.getIdempotencyKey());
        if (findOrder == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(clock);

        Order createOrder = Order.create(
            command.getUserId(),
            command.getProductId(),
            command.getProductPrice(),
            command.getQuantity(),
            command.getIdempotencyKey(),
            now,
            command.getStreet(),
            command.getCity(),
            command.getZipcode()
        );
        Order saveOrder = orderRepository.save(createOrder);

        CreatedOrderEvent event = CreatedOrderEvent.of(
            saveOrder.getId(),
            saveOrder.getUserId(),
            saveOrder.getTotalAmount(),
            UUID.randomUUID(),
            now,
            command.getExpiresAt()
        );

        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            saveOrder.getId(),
            EventType.ORDER_CREATED,
            UUID.randomUUID(),
            makePayload(event)
        );

        outboxRepository.save(outbox);
    }

    private String makePayload(CreatedOrderEvent event)  {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("이벤트 Payload JSON 변환 실패");
            throw new RuntimeException(e);
        }
    }

}

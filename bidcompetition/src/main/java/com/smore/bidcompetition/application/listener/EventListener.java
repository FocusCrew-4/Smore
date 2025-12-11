package com.smore.bidcompetition.application.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.bidcompetition.application.dto.BidCreateCommand;
import com.smore.bidcompetition.application.service.BidCompetitionService;
import com.smore.bidcompetition.infrastructure.persistence.event.inbound.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j(topic = "EventListener")
@Component
@RequiredArgsConstructor
public class EventListener {

    private final BidCompetitionService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(

        topics = "${topic.product.created}",
        groupId = "${consumer.group.product}",
        concurrency = "2"
    )
    public void productCreated(String message, Acknowledgment ack) {
        try {
            ProductCreatedEvent event = objectMapper.readValue(message,
                ProductCreatedEvent.class);

            BidCreateCommand command = BidCreateCommand.of(
                event.getProductId(),
                event.getCategoryId(),
                event.getSellerId(),
                event.getProductPrice(),
                event.getStock(),
                event.getStartAt(),
                event.getEndAt(),
                event.getIdempotencyKey()
            );

            service.createBid(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("productCreated 처리 실패 : {}", message, e);
        }
    }
}

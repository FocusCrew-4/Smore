package com.smore.product.infrastructure.consumer;

import com.smore.product.application.service.ProductService;
import com.smore.product.infrastructure.consumer.dto.BidLimitedSaleFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidLimitedSaleFinishedConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "bid.limitedSaleFinished.v1", groupId = "product-service")
    public void handleLimitedSaleFinished(BidLimitedSaleFinishedEvent event) {

        log.info("🎯 [Event Received] bid.limitedSaleFinished.v1 productId = {}", event.getProductId());

        // TODO: 판매 수량 반영 로직
        productService.applyFinishedSale(event);

        log.info("처리 완료: {}", event);
    }
}
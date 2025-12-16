package com.smore.bidcompetition.application.factory;

import com.smore.bidcompetition.application.handler.BidResultFinalizedHandler;
import com.smore.bidcompetition.application.handler.ProductInventoryAdjustedHandler;
import com.smore.bidcompetition.application.handler.OutboxHandler;
import com.smore.bidcompetition.application.handler.WinnerCreatedHandler;
import com.smore.bidcompetition.domain.model.Outbox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxHandlerFactory {

    @Value("${topic.bid.winner-confirm}")
    private String winerCreatedTopic;

    @Value("${topic.bid.product-inventory-adjusted}")
    private String productInventoryAdjustedTopic;

    @Value("${topic.bid.finished}")
    private String bidResultFinalizedTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxHandler from(Outbox outbox) {
        return switch (outbox.getEventType()) {
            case BID_WINNER_SELECTED -> new WinnerCreatedHandler(winerCreatedTopic, kafkaTemplate, outbox);
            case PRODUCT_INVENTORY_ADJUSTED -> new ProductInventoryAdjustedHandler(productInventoryAdjustedTopic, kafkaTemplate, outbox);
            case BID_RESULT_FINALIZED -> new BidResultFinalizedHandler(bidResultFinalizedTopic, kafkaTemplate, outbox);
            default -> throw new IllegalArgumentException(
                "지원되지 않은 이벤트입니다." + outbox.getEventType()
            );
        };
    }
}

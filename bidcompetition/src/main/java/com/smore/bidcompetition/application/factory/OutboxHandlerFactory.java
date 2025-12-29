package com.smore.bidcompetition.application.factory;

import com.smore.bidcompetition.application.handler.BidResultFinalizedHandler;
import com.smore.bidcompetition.application.handler.ClearStockHandler;
import com.smore.bidcompetition.application.handler.InventoryConfirmTimeout;
import com.smore.bidcompetition.application.handler.ProductInventoryAdjustedHandler;
import com.smore.bidcompetition.application.handler.OutboxHandler;
import com.smore.bidcompetition.application.handler.SaveStockHandler;
import com.smore.bidcompetition.application.handler.WinnerCreatedHandler;
import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.infrastructure.redis.StockRedisService;
import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.Tracer;
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

    @Value("${topic.bid.inventory-confirm-timeout}")
    private String bidInventoryConfirmTimeoutTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Tracer tracer;
    private final Propagator propagator;
    private final StockRedisService stockRedisService;

    public OutboxHandler from(Outbox outbox) {
        return switch (outbox.getEventType()) {
            case BID_WINNER_SELECTED -> new WinnerCreatedHandler(tracer, propagator, winerCreatedTopic, kafkaTemplate, outbox);
            case PRODUCT_INVENTORY_ADJUSTED -> new ProductInventoryAdjustedHandler(tracer, propagator, productInventoryAdjustedTopic, kafkaTemplate, outbox);
            case BID_RESULT_FINALIZED -> new BidResultFinalizedHandler(tracer, propagator, bidResultFinalizedTopic, kafkaTemplate, outbox);
            case BID_INVENTORY_CONFIRM_TIMEOUT -> new InventoryConfirmTimeout(tracer, propagator, bidInventoryConfirmTimeoutTopic, kafkaTemplate, outbox);
            case SAVE_STOCK -> new SaveStockHandler(tracer, propagator, stockRedisService, outbox);
            default -> throw new IllegalArgumentException(
                "지원되지 않은 이벤트입니다." + outbox.getEventType()
            );
        };
    }
}

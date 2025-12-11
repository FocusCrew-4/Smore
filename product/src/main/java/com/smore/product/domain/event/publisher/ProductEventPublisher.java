package com.smore.product.domain.event.publisher;

import com.smore.product.domain.event.AuctionPendingStartEvent;
import com.smore.product.domain.event.AuctionStartedEvent;
import com.smore.product.domain.event.LimitedSalePendingStartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAuctionPendingStart(AuctionPendingStartEvent event) {
        kafkaTemplate.send("product.auctionPendingStart.v1", event);
    }

    public void publishAuctionStarted(AuctionStartedEvent event) {
        kafkaTemplate.send("product.auctionStarted.v1", event);
    }

    public void publishLimitedSalePendingStart(LimitedSalePendingStartEvent event) {
        kafkaTemplate.send("product.limitedSalePendingStart.v1", event);
    }

//    public void publishBidLimitedSaleFinished(BidLimitedSaleFinishedEvent event) {
//        kafkaTemplate.send("bid.limitedSaleFinished.v1", event);
//    }
}

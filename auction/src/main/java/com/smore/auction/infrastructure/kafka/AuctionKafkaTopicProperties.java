package com.smore.auction.infrastructure.kafka;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "product.topic")
@Component
@Getter
@Setter
public class AuctionKafkaTopicProperties {

    private Map<String, String> productStarted;
}

package com.smore.seller.infrastructure.kafka;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "topic.produce")
@Component
@Getter
@Setter
public class SellerTopicProperties {

    private Map<String, String> sellerRegister;

}

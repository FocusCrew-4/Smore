package com.smore.seller.infrastructure.kafka;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "seller.topic")
@Component
@Getter
@Setter
public class SellerTopicProperties {

    private Map<String, String> sellerRegister;
    private Map<String, String> sellerSettlement;
    private Map<String, String> sellerDeadLetter;

}

package com.smore.seller.infrastructure.adaptor;

import com.smore.seller.application.port.SellerTopicPort;
import com.smore.seller.infrastructure.kafka.SellerTopicProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellerTopicPortImpl implements SellerTopicPort {

    private final SellerTopicProperties sellerTopicProperties;

    @Override
    public String getSellerRegisterTopic(String version) {
        return sellerTopicProperties.getSellerRegister().get(version);
    }
}

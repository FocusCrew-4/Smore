package com.smore.seller.infrastructure.adaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.seller.application.port.EventSerializerPort;
import com.smore.seller.infrastructure.kafka.SellerEventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventSerializerPortImpl implements EventSerializerPort {

    private final SellerEventSerializer serializer;

    @Override
    public String serializeEvent(Object event) {
        return serializer.toJsonString(event);
    }
}

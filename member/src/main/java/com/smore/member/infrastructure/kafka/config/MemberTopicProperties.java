package com.smore.member.infrastructure.kafka.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "member.topic")
@Component
@Getter
@Setter
public class MemberTopicProperties {

    private Map<String, String> sellerRegister;

}

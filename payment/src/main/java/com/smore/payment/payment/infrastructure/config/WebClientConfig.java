package com.smore.payment.payment.infrastructure.config;

import jakarta.inject.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder tossWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", "Basic {BASE64_ENCODED_SECRET_KEY}");
    }
}

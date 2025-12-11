package com.smore.payment.payment.infrastructure.config;

import jakarta.inject.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Value("${pg.secret-key}")
    private String pgSecretKey;


    @Bean
    public WebClient tossApproveWebClient() {
        String encoded = Base64.getEncoder()
                .encodeToString(("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:")
                        .getBytes(StandardCharsets.UTF_8));

        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient tossFailWebClient() {

        String encoded = Base64.getEncoder()
                .encodeToString(("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:")
                        .getBytes(StandardCharsets.UTF_8));

        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

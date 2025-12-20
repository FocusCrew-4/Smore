package com.smore.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// TODO: 공부 및 정리
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder plainWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @LoadBalanced  // Eureka 서비스명 기바능로 호출 가능하게 함
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

}

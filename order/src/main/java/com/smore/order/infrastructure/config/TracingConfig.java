package com.smore.order.infrastructure.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

@Configuration
public class TracingConfig {

    @Bean
    ObservationPredicate noActuatorOrScheduler() {
        return (name, context) -> {
            // 1. "task"로 시작하는 것(스케줄러) 무시
            if (name.startsWith("task")) {
                return false;
            }

            // 2. "/actuator"나 "/health" 같은 헬스 체크 URL 무시
            if (context instanceof ServerRequestObservationContext) {
                ServerRequestObservationContext serverContext = (ServerRequestObservationContext) context;
                String uri = serverContext.getCarrier().getRequestURI();
                if (uri.startsWith("/actuator") || uri.startsWith("/health")) {
                    return false;
                }
            }

            // 나머지는 통과 (기록함)
            return true;
        };
    }
}

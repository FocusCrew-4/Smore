package com.smore.auction.infrastructure.tracing;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

@Configuration
public class TracingConfig {

    @Bean
    ObservationPredicate noActuatorOrScheduler() {
        return (name, context) -> {
            if (name.startsWith("task")) {
                return false;
            }

            if (context instanceof ServerRequestObservationContext) {
                ServerRequestObservationContext serverContext =
                    (ServerRequestObservationContext) context;
                String uri = serverContext.getCarrier().getRequestURI();
                if (uri.startsWith("/actuator") || uri.startsWith("/health")) {
                    return false;
                }
            }

            return true;
        };
    }
}

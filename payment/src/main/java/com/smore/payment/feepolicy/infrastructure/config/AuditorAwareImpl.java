package com.smore.payment.feepolicy.infrastructure.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Long userId = 1L;
        return Optional.ofNullable(userId);
    }

}

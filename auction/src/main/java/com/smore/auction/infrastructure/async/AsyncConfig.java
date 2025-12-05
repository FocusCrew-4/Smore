package com.smore.auction.infrastructure.async;

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        // ThreadPoolTaskExecutor 은 Spring 이 제공하는 ThreadPool 구현체임
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(8);

        executor.setMaxPoolSize(12);

        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("Async-");

        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return ((ex, method, params) -> {
            log.error("비동기 작업 중 예외 발생 - Method: {}, Exception: {}",
                method.getName(), ex.getMessage());
            // 실무에서는 여기서 모니터링 시스템에 알림을 보내거나,
            // 재시도 로직을 구현할 수 있습니다.
        });
    }
}

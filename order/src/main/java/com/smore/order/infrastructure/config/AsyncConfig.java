package com.smore.order.infrastructure.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

    /**
     * 단기 급격한 트래픽 설정
     * 평소에는 적은 스레드로 리소스 절약
     * 최대 스레드 수를 높게 설정하여 트래픽 급증 시 빠르게 확장 가능
     * 대기열을 작게 하여 대기열이 빨리 차서 새 스레드 생성을 촉진
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("task-worker");
        executor.initialize();

        return executor;
    }

}

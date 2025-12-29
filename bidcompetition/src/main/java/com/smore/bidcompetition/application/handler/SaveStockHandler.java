package com.smore.bidcompetition.application.handler;

import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.domain.status.OutboxResult;
import com.smore.bidcompetition.infrastructure.redis.StockRedisService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "SaveStockHandler")
public class SaveStockHandler implements OutboxHandler{

    private final StockRedisService stockRedisService;
    private final Outbox outbox;
    private final Tracer tracer;
    private final Propagator propagator;

    public SaveStockHandler(Tracer tracer, Propagator propagator,
        StockRedisService stockRedisService, Outbox outbox) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.stockRedisService = stockRedisService;
        this.outbox = outbox;
    }

    @Override
    public OutboxResult execute() {

        Span newSpan = restoreAndStartSpan();

        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            try {
                long setResult = stockRedisService.setStock(outbox.getAggregateId(), Integer.parseInt(outbox.getPayload()));

                if (setResult == -1L) {
                    log.error("재고 초기화 실패: bidId={}, stock={}",outbox.getAggregateId(), Integer.parseInt(outbox.getPayload()));
                    return OutboxResult.FAIL;
                } else if (setResult == 0L) {
                    log.info("이미 재고 키가 존재합니다. bidId={}", outbox.getAggregateId(), Integer.parseInt(outbox.getPayload()));
                } else {
                    log.info("재고 초기화 완료: bidId={}, stock={}", outbox.getAggregateId(), Integer.parseInt(outbox.getPayload()));
                }
            } catch (Exception e) {
                log.error("재고 초기화 중 예외 발생. bidId={}", outbox.getAggregateId(), e);
                return OutboxResult.FAIL;
            }
            return OutboxResult.SUCCESS;
        } finally {
            newSpan.end();
        }
    }

    private Span restoreAndStartSpan() {
        Span.Builder spanBuilder = propagator.extract(outbox, (carrier, key) -> {
            if ("X-B3-TraceId".equalsIgnoreCase(key)) {
                return carrier.getTraceId();
            }
            if ("X-B3-SpanId".equalsIgnoreCase(key)) {
                return carrier.getSpanId();
            }
            return null;
        });

        Span newSpan = spanBuilder
            .name("redis-saved-stock")
            .start();
        return newSpan;
    }
}

package com.smore.bidcompetition.application.handler;

import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.domain.status.OutboxResult;
import com.smore.bidcompetition.infrastructure.redis.StockRedisService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "ClearStockHandler")
public class ClearStockHandler implements OutboxHandler{

    private final StockRedisService stockRedisService;
    private final Outbox outbox;
    private final Tracer tracer;
    private final Propagator propagator;

    public ClearStockHandler(Tracer tracer, Propagator propagator,
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
                boolean deleted = stockRedisService.deleteStock(outbox.getAggregateId());
                if (!deleted) {
                    log.info("stock 키가 없어 삭제할 게 없습니다. bidId={}", outbox.getAggregateId());
                } else {
                    log.info("stock 키 삭제 완료. bidId={}", outbox.getAggregateId());
                }
                return OutboxResult.SUCCESS;
            } catch (Exception e) {
                log.error("stock 키 삭제 중 예외. bidId={}", outbox.getAggregateId(), e);
                return OutboxResult.FAIL;
            }
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
            .name("redis-clear-stock")
            .start();
        return newSpan;
    }
}

package com.smore.common.event;

import java.time.Instant;
import java.util.UUID;

public abstract class CommonKafkaEvent<T> {
    // Kafka 에 보낼때 어차피 CommonKafkaEvent.getTopic, key, CommonKafkaEvent 로 사용되어 빼도 될거같다
    // key 는 파티션 결정 및 메시지 그룹화를 위한 식별자로
    // 일반적으로 사용자 ID, 주문 ID, 도메인 고유 ID 등을 할당해 같은 엔티티의 메시지들이 동일 파티션에 가도록 설계한다고 합니다
    private final String topic;
    private final T payload;

    // 이벤트 카테고리 구분용 (ex: product -> AUCTION_START -> auction)
    private final Enum<?> type;

    // 메시지 소스나 발행 서비스 이름 등 헤더에 기록할 용도
    private final String source;

    // 멱등성과 파티셔닝 키로 사용될 값 (Kafka key)
    private final String idempotencyKey;

    // LocalDateTime 은 TimeZone 의존도가 높아 UTC 기준 시간 기록을 위해 Instant 사용 권장
    private final Instant timestamp;

    protected CommonKafkaEvent(String topic, T payload, Enum<?> type, String source, String idempotencyKey) {
        this.topic = topic;
        this.payload = payload;
        this.type = type;
        this.source = source;
        this.idempotencyKey = idempotencyKey;
        this.timestamp = Instant.now();
    }

    /**
     * 랜덤 키가 허용되는 경우에만 사용하세요. 멱등성/파티셔닝이 중요하면 명시적 키를 넘기십시오.
     */
    protected CommonKafkaEvent(String topic, T payload, Enum<?> type, String source) {
        this(topic, payload, type, source, UUID.randomUUID().toString());
    }

    public String getTopic() {
        return topic;
    }

    public T getPayload() {
        return payload;
    }

    public Enum<?> getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}

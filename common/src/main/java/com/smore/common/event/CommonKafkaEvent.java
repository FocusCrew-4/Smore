package com.smore.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class CommonKafkaEvent<T> {
    // Kafka 에 보낼때 어차피 CommonKafkaEvent.getTopic, key, CommonKafkaEvent 로 사용되어 빼도 될거같다
    // key 는 파티션 결정 및 메시지 그룹화를 위한 식별자로
    // 일반적으로 사용자 ID, 주문 ID, 도메인 고유 ID 등을 할당해 같은 엔티티의 메시지들이 동일 파티션에 가도록 설계한다고 합니다
    private final String topic;
    private final T payload;

    // 종류 구분 (도메인 이벤트 타입 ex:product -> AUCTION_START -> auction)
    private final Enum<?> type;

    // header 에 넣는 방법도 있다고 합니다
    private final String source;

    private final UUID equalKey;
    private final LocalDateTime timestamp;

    CommonKafkaEvent(String topic, T payload, Enum<?> type, String source) {
        this.topic = topic;
        this.payload = payload;
        this.type = type;
        this.source = source;

        this.equalKey = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
    }

    public UUID getEqualKey() {
        return equalKey;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public T getPayload() {
        return payload;
    }
}
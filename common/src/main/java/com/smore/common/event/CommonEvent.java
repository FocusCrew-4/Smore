package com.smore.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommonEvent<T> {
    private String topic;
    private T payload;

    // 종류 구분 (도메인 이벤트 타입 ex:product -> AUCTION_START -> auction)
    private Enum<?> type;

    // header 에 넣는 방법도 있다고 합니다
    private String source;

    private UUID equalKey;
    private LocalDateTime timestamp;
}
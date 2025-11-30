package com.smore.order.infrastructure.persistence.repository.outbox;

import static org.junit.jupiter.api.Assertions.*;

import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.AggregateType;
import com.smore.order.domain.status.EventType;
import com.smore.order.infrastructure.config.JpaConfig;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(
    {
        JpaConfig.class,
        OutboxRepositoryImpl.class
    }
)
@ActiveProfiles("test")
class OutboxRepositoryImplTest {

    @Autowired
    OutboxRepository outboxRepository;

    @Autowired
    EntityManager em;

    @DisplayName("outbox에 적합한 데이터가 삽입되면 outbox 테이블에 정상 등록됩니다.")
    @Test
    void outboxSave() {
        // given
        AggregateType aggregateType = AggregateType.ORDER;
        UUID aggregateId = UUID.randomUUID();
        EventType eventType = EventType.ORDER_CREATED;
        UUID idempotencyKey = UUID.randomUUID();
        String payload = "테스트 데이터";

        Outbox outbox = Outbox.create(
            aggregateType,
            aggregateId,
            eventType,
            idempotencyKey,
            payload
        );

        // when
        outbox = outboxRepository.save(outbox);

        // then
        Assertions.assertThat(outbox.getId()).isNotNull();
        Assertions.assertThat(outbox.getAggregateId()).isEqualTo(aggregateId);
        Assertions.assertThat(outbox.getAggregateType()).isEqualTo(aggregateType);
        Assertions.assertThat(outbox.getEventType()).isEqualTo(eventType);
        Assertions.assertThat(outbox.getIdempotencyKey()).isEqualTo(idempotencyKey);
        Assertions.assertThat(outbox.getPayload()).isEqualTo(payload);
    }

    @DisplayName("save() 메서드의 인자가 null인 경우 IllegalArgumentException 예외하여 저장되지 않는다.")
    @Test
    void outboxSaveWhenOutboxIsNull() {

        Assertions.assertThatThrownBy(
                () -> outboxRepository.save(null)
            ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("outbox null입니다.");
    }

}
package com.smore.order.infrastructure.persistence.repository.outbox;

import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.AggregateType;
import com.smore.order.domain.status.EventStatus;
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
        Assertions.assertThat(outbox.getEventStatus()).isEqualTo(EventStatus.PENDING);
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

    @DisplayName("선점 요청이 들어오면 EventStatus 값이 PROCESSING이 된다. ")
    @Test
    void claimTest() {
        // given

        Outbox outbox =  Outbox.create(
            AggregateType.ORDER,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            EventType.ORDER_CREATED,
            UUID.fromString("3f9b8c1d-4e7e-4c1a-a8fd-2a6a7f9c3b44"),
            "Test Payload Data"
        );

        Outbox saveOutbox = outboxRepository.save(outbox);

        // when
        int result =  outboxRepository.claim(saveOutbox.getId(), EventStatus.PROCESSING);

        Outbox fresh = outboxRepository.findById(saveOutbox.getId());

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(fresh.getEventStatus()).isEqualTo(EventStatus.PROCESSING);
    }

    @DisplayName("선점 요청할 때 outboxId가 null인 경우 IllegalArgumentException가 발생하고 선점하지 못한다.")
    @Test
    void claimTestWithException() {
        // when // then
        Assertions.assertThatThrownBy(
                () -> outboxRepository.claim(null, EventStatus.PROCESSING)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("outboxId가 null입니다.");
    }

    @DisplayName("선점 후 markSent를 호출하면 EventStatus 값이 SENT가 된다.")
    @Test
    void markSentTest() {
        // given
        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            EventType.ORDER_CREATED,
            UUID.fromString("3f9b8c1d-4e7e-4c1a-a8fd-2a6a7f9c3b44"),
            "Test Payload Data"
        );

        Outbox saveOutbox = outboxRepository.save(outbox);

        outboxRepository.claim(saveOutbox.getId(), EventStatus.PROCESSING);

        // when
        int result = outboxRepository.markSent(saveOutbox.getId(), EventStatus.SENT);
        Outbox fresh = outboxRepository.findById(saveOutbox.getId());

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(fresh.getEventStatus()).isEqualTo(EventStatus.SENT);
    }

    @DisplayName("markSent 호출할 때 outboxId가 null인 경우 IllegalArgumentException가 발생하고 상태를 변경하지 못한다.")
    @Test
    void markSentTestWithException() {
        // when // then
        Assertions.assertThatThrownBy(
                () -> outboxRepository.markSent(null, EventStatus.SENT)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("outboxId가 null입니다.");
    }

    @DisplayName("makeRetry를 호출하면 상태가 변경되고 retryCount가 1 증가한다.")
    @Test
    void markRetryTest() {
        // given
        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            EventType.ORDER_CREATED,
            UUID.fromString("3f9b8c1d-4e7e-4c1a-a8fd-2a6a7f9c3b44"),
            "Test Payload Data"
        );

        Outbox saveOutbox = outboxRepository.save(outbox);

        outboxRepository.claim(saveOutbox.getId(), EventStatus.PROCESSING);

        Outbox before = outboxRepository.findById(saveOutbox.getId());
        int beforeRetryCount = before.getRetryCount();

        // when
        int result = outboxRepository.markRetry(saveOutbox.getId(), EventStatus.PROCESSING);

        Outbox fresh = outboxRepository.findById(saveOutbox.getId());

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(fresh.getEventStatus()).isEqualTo(EventStatus.PROCESSING);
        Assertions.assertThat(fresh.getRetryCount()).isEqualTo(beforeRetryCount + 1);
    }

    @DisplayName("makeRetry 호출할 때 outboxId가 null인 경우 IllegalArgumentException가 발생하고 상태를 변경하지 못한다.")
    @Test
    void markRetryTestWithException() {
        // when // then
        Assertions.assertThatThrownBy(
                () -> outboxRepository.markRetry(null, EventStatus.PROCESSING)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("outboxId가 null입니다.");
    }

    @DisplayName("retryCount가 maxRetryCount 이상인 상태에서 makeFail을 호출하면 EventStatus 값이 FAIL이 된다.")
    @Test
    void markFailTest() {
        // given
        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            EventType.ORDER_CREATED,
            UUID.fromString("3f9b8c1d-4e7e-4c1a-a8fd-2a6a7f9c3b44"),
            "Test Payload Data"
        );

        Outbox saveOutbox = outboxRepository.save(outbox);

        outboxRepository.claim(saveOutbox.getId(), EventStatus.PROCESSING);

        outboxRepository.markRetry(saveOutbox.getId(), EventStatus.PROCESSING);

        int maxRetryCount = 1;

        // when
        int result = outboxRepository.markFail(saveOutbox.getId(), EventStatus.FAILED, maxRetryCount);

        Outbox fresh = outboxRepository.findById(saveOutbox.getId());

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(fresh.getEventStatus()).isEqualTo(EventStatus.FAILED);
        Assertions.assertThat(fresh.getRetryCount()).isGreaterThanOrEqualTo(maxRetryCount);
    }

    @DisplayName("makeFail 호출할 때 outboxId가 null인 경우 IllegalArgumentException가 발생하고 상태를 변경하지 못한다.")
    @Test
    void markFailTestWithNullIdException() {
        // when // then
        Assertions.assertThatThrownBy(
                () -> outboxRepository.markFail(null, EventStatus.FAILED, 1)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("outboxId가 null입니다.");
    }

    @DisplayName("makeFail 호출할 때 maxRetryCount가 null이거나 1보다 작으면 IllegalArgumentException가 발생한다.")
    @Test
    void markFailTestWithInvalidMaxRetryCountException() {
        // given
        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            EventType.ORDER_CREATED,
            UUID.fromString("3f9b8c1d-4e7e-4c1a-a8fd-2a6a7f9c3b44"),
            "Test Payload Data"
        );
        Outbox saveOutbox = outboxRepository.save(outbox);

        // when // then
        Assertions.assertThatThrownBy(
                () -> outboxRepository.markFail(saveOutbox.getId(), EventStatus.FAILED, null)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("maxRetryCount 값은 필수이며 양수여야 합니다.");

        // when // then
        Assertions.assertThatThrownBy(
                () -> outboxRepository.markFail(saveOutbox.getId(), EventStatus.FAILED, 0)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("maxRetryCount 값은 필수이며 양수여야 합니다.");
    }

}
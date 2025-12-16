package com.smore.seller.infrastructure.kafka.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.seller.application.port.in.InboxService;
import com.smore.seller.application.usecase.CreditSellerMoney;
import com.smore.seller.infrastructure.kafka.SellerTopicProperties;
import com.smore.seller.infrastructure.kafka.listener.dto.SettlementV1;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class SellerKafkaListenerTest {

    private static final String DEAD_LETTER_TOPIC = "seller.deadletter.v1";

    @Mock
    private CreditSellerMoney creditSellerMoney;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private InboxService inboxService;

    @Mock
    private Acknowledgment ack;

    private ObjectMapper objectMapper;
    private SellerKafkaListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SellerTopicProperties topic = new SellerTopicProperties();
        topic.setSellerDeadLetter(Map.of("v1", DEAD_LETTER_TOPIC));

        listener = new SellerKafkaListener(
            objectMapper,
            creditSellerMoney,
            kafkaTemplate,
            topic,
            inboxService
        );
    }

    @Test
    @DisplayName("정상흐름 function 이 1번 호출, ack 호출, DLT 발행 X")
    void paymentSettlementV1_processesOnceAndAcknowledges() throws Exception {
        SettlementV1 event = new SettlementV1(
            1L,
            new BigDecimal("123.45"),
            UUID.randomUUID()
        );
        String payload = objectMapper.writeValueAsString(event);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(inboxService).processOnce(eq(event.idempotencyKey()), any());

        listener.paymentSettlementV1(payload, ack);

        verify(inboxService).processOnce(eq(event.idempotencyKey()), any());
        verify(creditSellerMoney).credit(event.id(), event.amount());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        verify(ack).acknowledge();
    }

    @Test
    @DisplayName("멱등키가 같은 이벤트를 listen 하였을때, processOnce 는 두 번 호출, function 은 1번 호출")
    void paymentSettlementV1_isIdempotentForSameIdempotencyKey() throws Exception {
        SettlementV1 event = new SettlementV1(
            7L,
            new BigDecimal("5.00"),
            UUID.randomUUID()
        );
        String payload = objectMapper.writeValueAsString(event);
        AtomicInteger processCallCount = new AtomicInteger();

        doAnswer(invocation -> {
            if (processCallCount.getAndIncrement() == 0) {
                invocation.<Runnable>getArgument(1).run();
            }
            return null;
        }).when(inboxService).processOnce(eq(event.idempotencyKey()), any());

        listener.paymentSettlementV1(payload, ack);
        listener.paymentSettlementV1(payload, ack);

        verify(inboxService, times(2)).processOnce(eq(event.idempotencyKey()), any());
        verify(creditSellerMoney, times(1)).credit(event.id(), event.amount());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        verify(ack, times(2)).acknowledge();
    }

    @Test
    @DisplayName("역직렬화 예외 발생시 DLT 전송 및 ack 호출")
    void paymentSettlementFailedV1_sendsToDeadLetterOnDeserializationError() {
        String invalidPayload = "{invalid json";

        listener.paymentSettlementFailedV1(invalidPayload, ack);

        verifyNoInteractions(inboxService, creditSellerMoney);
        verify(kafkaTemplate).send(DEAD_LETTER_TOPIC, invalidPayload);
        verify(ack).acknowledge();
    }
}

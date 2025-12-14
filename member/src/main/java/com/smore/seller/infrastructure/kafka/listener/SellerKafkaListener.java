package com.smore.seller.infrastructure.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.seller.application.port.in.InboxService;
import com.smore.seller.application.usecase.CreditSellerMoney;
import com.smore.seller.infrastructure.kafka.SellerTopicProperties;
import com.smore.seller.infrastructure.kafka.listener.dto.SettlementFailedV1;
import com.smore.seller.infrastructure.kafka.listener.dto.SettlementV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
@RequiredArgsConstructor
public class SellerKafkaListener {

    private final ObjectMapper objectMapper;
    private final CreditSellerMoney creditSellerMoney;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SellerTopicProperties topic;
    private final InboxService inboxService;

    @KafkaListener(
        topics = "payment.settlement-failed.v1"
    )

    // 회원이 정산 요청을 보냈지만 정산이 실패한 경우 돈+
    public void paymentSettlementFailedV1(String event, Acknowledgment ack) {
        log.info("Received event {}", event);
        try {
            var paymentSettlementFailed
                = objectMapper.readValue(event, SettlementFailedV1.class);

            log.info("AuctionStartedV1 deserialize {}", paymentSettlementFailed);

            inboxService.processOnce(
                paymentSettlementFailed.idempotencyKey(),
                () -> creditSellerMoney.credit(
                    paymentSettlementFailed.id(),
                    paymentSettlementFailed.amount()
                )
            );

            ack.acknowledge();
        } catch (Exception e) {
            log.error("PaymentSettlementFailedV1 - error: {}", e.getMessage());
            kafkaTemplate.send(
                topic.getSellerDeadLetter().get("v1"),
                event
            );
            ack.acknowledge();
        }
    }

    // 결제성공하여 seller 지갑에 돈+
    @KafkaListener(
        topics = "payment.settlement.v1"
    )
    public void paymentSettlementV1(String event, Acknowledgment ack) {
        log.info("Received event {}", event);

        try {
            var settlement
                = objectMapper.readValue(event, SettlementV1.class);

            log.info("AuctionStartedV1 deserialize {}", settlement);

            // 멱등검사
            inboxService.processOnce(
                settlement.idempotencyKey(),
                () -> creditSellerMoney.credit(
                    settlement.id(),
                    settlement.amount()
                )
            );

            ack.acknowledge();

        } catch (JsonProcessingException | RuntimeException e) {
            log.error("PaymentSettlementFailedV1 - error: {}", e.getMessage());
            // 수복 불가능한 예외는 DLT 로 던짐
            kafkaTemplate.send(
                topic.getSellerDeadLetter().get("v1"),
                event
            );
            ack.acknowledge();
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            // 리트라이등의 처리는 추후 DefaultErrorHandler 를 추가하여 전역처리 예정입니다
            ack.acknowledge();
        }
    }
}

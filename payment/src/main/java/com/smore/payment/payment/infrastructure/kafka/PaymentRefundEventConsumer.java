//package com.smore.payment.payment.infrastructure.kafka;
//
//import com.smore.payment.payment.application.event.inbound.PaymentRequestedEvent;
//import com.smore.payment.payment.infrastructure.kafka.dto.PaymentCreateRequestEvent;
//import com.smore.payment.payment.infrastructure.kafka.dto.PaymentRefundRequestEvent;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class PaymentRefundEventConsumer {
//
//    private final PaymentRefundService paymentRefundService;
//
//    @KafkaListener(topics = "order.canceled.v1")
//    public void handle(PaymentRefundRequestEvent event, Acknowledgment ack) {
//        log.info("PaymentRequestedEvent 수신: orderId={}, amount={}, expiredAt={}",
//                event.getOrderId(), event.getTotalAmount(), event.getExpiresAt());
//
//        PaymentRequestedEvent paymentRequestedEvent = PaymentRequestedEvent.of(
//                event.getOrderId(),
//                event.getUserId(),
//                BigDecimal.valueOf(event.getTotalAmount()),
//                event.getIdempotencyKey(),
//                event.getSellerId(),
//                event.getCategoryId(),
//                event.getSaleType(),
//                event.getPublishedAt(),
//                event.getExpiresAt()
//        );
//
//        if (LocalDateTime.now().isAfter(event.getExpiresAt())) {
//            log.warn("결제 요청 만료됨 — orderId={}, expiredAt={} (now={})",
//                    event.getOrderId(), event.getExpiresAt(), LocalDateTime.now());
//            ack.acknowledge();
//            return;
//        }
//
//        // 만료되지 않았으면 정상 처리
//        createTemporaryPaymentService.create(paymentRequestedEvent);
//        ack.acknowledge();
//    }
//}

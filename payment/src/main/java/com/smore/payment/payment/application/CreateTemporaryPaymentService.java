package com.smore.payment.payment.application;

import com.smore.payment.payment.application.event.inbound.PaymentRequestedEvent;
import com.smore.payment.payment.infrastructure.persistence.redis.model.TemporaryPayment;
import com.smore.payment.payment.domain.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTemporaryPaymentService {

    private final RedisRepository redisRepository;

    @Transactional
    public void create(PaymentRequestedEvent paymentRequestedEvent) {

        if (redisRepository.existsByOrderId(paymentRequestedEvent.orderId())) {
            return;
        }

        TemporaryPayment temp = TemporaryPayment.create(
                paymentRequestedEvent.idempotencyKey(),
                paymentRequestedEvent.orderId(),
                paymentRequestedEvent.userId(),
                paymentRequestedEvent.amount(),
                paymentRequestedEvent.sellerId(),
                paymentRequestedEvent.categoryId(),
                paymentRequestedEvent.auctionType(),
                paymentRequestedEvent.expiredAt()
        );

        redisRepository.save(temp);
    }
}

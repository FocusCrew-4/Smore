package com.smore.payment.payment.application;

import com.smore.payment.payment.domain.event.PaymentRequestedEvent;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTemporaryPaymentService {

    private final RedisRepository redisRepository;

    @Transactional
    public void create(PaymentRequestedEvent event) {

        if (redisRepository.existsByOrderId(event.getOrderId())) {
            return;
        }

        TemporaryPayment temp = TemporaryPayment.create(
                event.getIdempotencyKey(),
                event.getOrderId(),
                event.getUserId(),
                event.getAmount()
        );

        redisRepository.save(temp);
    }
}

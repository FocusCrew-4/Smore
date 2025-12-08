package com.smore.payment.payment.domain.repository;

import com.smore.payment.payment.domain.model.Payment;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {
    void save(Payment payment);
}

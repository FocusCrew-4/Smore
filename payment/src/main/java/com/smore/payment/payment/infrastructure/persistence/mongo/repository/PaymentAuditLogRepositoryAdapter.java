package com.smore.payment.payment.infrastructure.persistence.mongo.repository;

import com.smore.payment.payment.application.port.out.PaymentAuditLogPort;
import com.smore.payment.payment.infrastructure.persistence.mongo.model.PaymentAuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentAuditLogRepositoryAdapter implements PaymentAuditLogPort {

    private final PaymentAuditLogRepository paymentAuditLogRepository;

    @Override
    public void save(PaymentAuditLog auditLog) {
        paymentAuditLogRepository.save(auditLog);
    }
}
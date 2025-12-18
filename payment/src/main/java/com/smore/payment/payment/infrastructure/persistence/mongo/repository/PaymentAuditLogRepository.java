package com.smore.payment.payment.infrastructure.persistence.mongo.repository;

import com.smore.payment.payment.infrastructure.persistence.mongo.model.PaymentAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentAuditLogRepository extends MongoRepository<PaymentAuditLog, String> {
}
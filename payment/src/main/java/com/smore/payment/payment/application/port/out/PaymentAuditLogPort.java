package com.smore.payment.payment.application.port.out;

import com.smore.payment.payment.infrastructure.persistence.mongo.model.PaymentAuditLog;

public interface PaymentAuditLogPort {

    void save(PaymentAuditLog auditLog);
}
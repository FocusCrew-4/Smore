package com.smore.payment.payment.infrastructure.persistence.repository.mongo;

import com.smore.payment.payment.domain.document.PgApproveLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentMongoRepository extends MongoRepository<PgApproveLog, String> {
}

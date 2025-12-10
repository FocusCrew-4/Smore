package com.smore.payment.payment.infrastructure.persistence.repository.mongo;

import com.smore.payment.payment.domain.document.PgApproveLog;
import com.smore.payment.payment.domain.repository.MongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MongoRepositoryImpl implements MongoRepository {

    private final PaymentMongoRepository paymentMongoRepository;

    @Override
    public void savePgApproveLog(PgApproveLog pgApproveLog) {
        paymentMongoRepository.save(pgApproveLog);
    }
}

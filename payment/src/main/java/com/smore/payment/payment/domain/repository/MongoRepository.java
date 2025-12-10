package com.smore.payment.payment.domain.repository;

import com.smore.payment.payment.domain.document.PgApproveLog;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRepository {

    void savePgApproveLog(PgApproveLog pgApproveLog);
}

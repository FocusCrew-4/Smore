package com.smore.payment.payment.infrastructure.pg.mapper;

import com.smore.payment.payment.domain.model.PgApproveResult;
import com.smore.payment.payment.infrastructure.pg.dto.TossApproveResponse;

public class TossPgMapper {

    public static PgApproveResult toDomain(TossApproveResponse res) {

        return new PgApproveResult(
                "TOSS",
                res.orderId(),
                res.transactionKey(),
                res.status(),
                "SUCCESS",

                // 카드 정보
                res.card().company(),
                res.card().number(),
                res.card().installmentPlanMonths(),
                res.card().isInterestFree(),
                res.card().cardType(),
                res.card().ownerType(),
                res.card().acquirerCode(),

                res.approvedAt()
        );
    }
}

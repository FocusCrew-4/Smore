package com.smore.payment.payment.infrastructure.pg.mapper;

import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.infrastructure.pg.dto.TossPaymentResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TossPgMapper {

    public static PgResponseResult toDomain(TossPaymentResponse res) {
        OffsetDateTime requested = OffsetDateTime.parse(res.requestedAt());
        OffsetDateTime approved = OffsetDateTime.parse(res.approvedAt());

        return new PgResponseResult(
                "tosspayments",
                res.paymentKey(),
                res.orderId(),
                res.orderName(),
                res.lastTransactionKey(),
                res.status(),
                res.method(),
                res.currency(),
                BigDecimal.valueOf(res.totalAmount()),
                BigDecimal.valueOf(res.balanceAmount()),

                res.card() != null ? res.card().issuerCode() : null,
                res.card() != null ? res.card().acquirerCode() : null,
                res.card() != null ? res.card().number() : null,
                res.card() != null ? res.card().installmentPlanMonths() : null,
                res.card() != null && res.card().isInterestFree(),
                res.card() != null ? res.card().approveNo() : null,
                res.card() != null ? res.card().cardType() : null,
                res.card() != null ? res.card().ownerType() : null,
                res.card() != null ? res.card().acquireStatus() : null,
                res.card() != null ? BigDecimal.valueOf(res.card().amount()) : null,

                requested.toLocalDateTime(),
                approved.toLocalDateTime(),

                res.failure() != null ? res.failure().code() : null,
                res.failure() != null ? res.failure().message() : null,
                res.cancels() != null
                        ? new PgResponseResult.CancellationInfo(
                        BigDecimal.valueOf(res.cancels().cancelAmount()),
                        res.cancels().cancelReason(),
                        BigDecimal.valueOf(res.cancels().refundableAmount()),
                        OffsetDateTime.parse(res.cancels().canceledAt())
                                .toLocalDateTime(),
                        res.cancels().transactionKey(),
                        res.cancels().cancelStatus()
                ) : null
        );
    }
}


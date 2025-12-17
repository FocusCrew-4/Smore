CREATE TABLE fee_policies
(
    id           UUID PRIMARY KEY,
    target_type  VARCHAR(50)  NOT NULL,
    target_key   VARCHAR(255) NOT NULL,
    fee_type     VARCHAR(50)  NOT NULL,
    rate         NUMERIC(10, 4),
    fixed_amount NUMERIC(19, 2),
    active       BOOLEAN      NOT NULL,
    create_by    BIGINT,
    created_at   TIMESTAMP,
    updated_by   BIGINT,
    updated_at   TIMESTAMP,
    deleted_by   BIGINT,
    deleted_at   TIMESTAMP
);

CREATE TABLE cancel_policies
(
    id                   UUID PRIMARY KEY,
    cancel_target_type   VARCHAR(50)  NOT NULL,
    target_key           VARCHAR(255) NOT NULL,
    cancel_limit_minutes BIGINT,
    cancel_fee_type      VARCHAR(50)  NOT NULL,
    rate                 NUMERIC(10, 4),
    fixed_amount         NUMERIC(19, 2),
    cancellable          BOOLEAN      NOT NULL,
    active               BOOLEAN      NOT NULL,
    create_by            BIGINT,
    created_at           TIMESTAMP,
    updated_by           BIGINT,
    updated_at           TIMESTAMP,
    deleted_by           BIGINT,
    deleted_at           TIMESTAMP
);

CREATE TABLE refund_policies
(
    id                 UUID PRIMARY KEY,
    refund_target_type VARCHAR(50)  NOT NULL,
    target_key         VARCHAR(255) NOT NULL,
    refund_period_days BIGINT,
    refund_fee_type    VARCHAR(50)  NOT NULL,
    rate               NUMERIC(10, 4),
    fixed_amount       NUMERIC(19, 2),
    refundable         BOOLEAN      NOT NULL,
    active             BOOLEAN      NOT NULL,
    create_by          BIGINT,
    created_at         TIMESTAMP,
    updated_by         BIGINT,
    updated_at         TIMESTAMP,
    deleted_by         BIGINT,
    deleted_at         TIMESTAMP
);

CREATE TABLE payments
(
    id                        UUID PRIMARY KEY,
    idempotency_key           UUID           NOT NULL,
    user_id                   BIGINT         NOT NULL,
    order_id                  UUID           NOT NULL,
    amount                    NUMERIC(19, 2) NOT NULL,
    payment_method            VARCHAR(50),
    status                    VARCHAR(50),
    approved_at               TIMESTAMP,
    card_issuer_code          VARCHAR(50),
    card_acquirer_code        VARCHAR(50),
    card_number               VARCHAR(50),
    installment_months        INTEGER,
    interest_free             BOOLEAN,
    approve_no                VARCHAR(50),
    card_type                 VARCHAR(50),
    card_owner_type           VARCHAR(50),
    card_acquire_status       VARCHAR(50),
    card_amount               NUMERIC(19, 2),
    failure_reason            VARCHAR(255),
    failed_at                 TIMESTAMP,
    cancel_reason             VARCHAR(255),
    cancel_amount             NUMERIC(19, 2),
    cancelled_at              TIMESTAMP,
    refund_reason             VARCHAR(255),
    refund_amount             NUMERIC(19, 2),
    refunded_at               TIMESTAMP,
    pg_cancel_transaction_key VARCHAR(255),
    refundable_amount         NUMERIC(19, 2),
    pg_provider               VARCHAR(100),
    payment_key               VARCHAR(100),
    pg_order_id               VARCHAR(100),
    pg_order_name             VARCHAR(255),
    pg_transaction_key        VARCHAR(100),
    pg_status                 VARCHAR(100),
    currency                  VARCHAR(10),
    pg_total_amount           NUMERIC(19, 2),
    pg_balance_amount         NUMERIC(19, 2),
    seller_id                 BIGINT         NOT NULL,
    category                  UUID           NOT NULL,
    auction_type              VARCHAR(50),
    CONSTRAINT uk_payments_idempotency_key UNIQUE (idempotency_key)
);

CREATE TABLE outboxes
(
    id              BIGSERIAL PRIMARY KEY,
    aggregate_type  VARCHAR(100),
    aggregate_id    UUID,
    event_type      VARCHAR(100),
    idempotency_key UUID,
    topic_name      VARCHAR(255),
    payload         TEXT,
    retry_count     INTEGER,
    status          VARCHAR(50),
    created_at      TIMESTAMP
);
package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    // 결제 정보
    private final UUID id;
    private final Long userId;
    private final BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime approvedAt;

    // 카드 정보
    private String cardCompany;
    private String cardNumber;
    private Integer installmentMonths;
    private boolean interestFree;
    private String cardType;
    private String ownerType;
    private String cardCompanyCode;
    private String acquirerCode;

    // 실패 / 취소 / 환불
    private PaymentFailure failure;
    private PaymentCancel cancel;
    private PaymentRefund refund;

    // PG 정보
    private String pgProvider;
    private String pgOrderId;
    private String pgTransactionKey;
    private String pgStatus;
    private String pgMessage;
}

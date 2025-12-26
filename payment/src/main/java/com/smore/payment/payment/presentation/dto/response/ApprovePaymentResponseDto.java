package com.smore.payment.payment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovePaymentResponseDto {

    private boolean success;
    private String status;

    private String paymentId;
    private String orderId;

    private BigDecimal approvedAmount;
    private LocalDateTime approvedAt;
}

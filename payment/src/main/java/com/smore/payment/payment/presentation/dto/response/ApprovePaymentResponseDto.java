package com.smore.payment.payment.presentation.dto.response;

import com.smore.payment.payment.domain.model.Payment;
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

    public static ApprovePaymentResponseDto from(Payment payment) {
        return new ApprovePaymentResponseDto(
                true,
                payment.getStatus().name(),

                payment.getId().toString(),
                payment.getOrderId().toString(),

                payment.getAmount(),
                payment.getApprovedAt()
        );
    }
}

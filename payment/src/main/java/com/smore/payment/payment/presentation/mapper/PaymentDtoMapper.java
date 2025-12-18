package com.smore.payment.payment.presentation.mapper;

import com.smore.payment.payment.application.port.in.ApprovePaymentCommand;
import com.smore.payment.payment.application.port.in.ApprovePaymentResult;
import com.smore.payment.payment.presentation.dto.request.ApprovePaymentRequestDto;
import com.smore.payment.payment.presentation.dto.response.ApprovePaymentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentDtoMapper {

    public ApprovePaymentCommand toCommand(ApprovePaymentRequestDto requestDto) {
        return new ApprovePaymentCommand(
                requestDto.getOrderId(),
                requestDto.getIdempotencyKey(),
                requestDto.getAmount(),
                requestDto.getPaymentKey(),
                requestDto.getPgOrderId()
        );
    }

    public ApprovePaymentResponseDto toResponseDto(ApprovePaymentResult result) {
        return new ApprovePaymentResponseDto(
                true,
                result.status(),
                result.paymentId().toString(),
                result.orderId().toString(),
                result.approvedAmount(),
                result.approvedAt()
        );
    }
}
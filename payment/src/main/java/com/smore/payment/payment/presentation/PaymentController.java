package com.smore.payment.payment.presentation;

import com.smore.common.response.ApiResponse;
import com.smore.payment.payment.application.port.in.ApprovePaymentResult;
import com.smore.payment.payment.application.port.in.ApprovePaymentUseCase;
import com.smore.payment.payment.presentation.dto.request.ApprovePaymentRequestDto;
import com.smore.payment.payment.presentation.dto.response.ApprovePaymentResponseDto;
import com.smore.payment.payment.presentation.mapper.PaymentDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final ApprovePaymentUseCase approvePaymentUseCase;
    private final PaymentDtoMapper paymentDtoMapper;

    @PostMapping("/approve")
    public ResponseEntity<?> approvePayment(
            @Valid @RequestBody ApprovePaymentRequestDto request
    ) {
        ApprovePaymentResult result = approvePaymentUseCase.approve(paymentDtoMapper.toCommand(request));
        ApprovePaymentResponseDto response = paymentDtoMapper.toResponseDto(result);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}


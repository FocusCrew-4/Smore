package com.smore.payment.payment.presentation;

import com.smore.common.response.ApiResponse;
import com.smore.payment.payment.application.CreatePaymentService;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.presentation.dto.request.ApprovePaymentRequestDto;
import com.smore.payment.payment.presentation.dto.response.ApprovePaymentResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final CreatePaymentService createPaymentService;

    @PostMapping("/approve")
    public ResponseEntity<?> approvePayment(
            @Valid @RequestBody ApprovePaymentRequestDto request
    ) {
        Payment payment = createPaymentService.approve(request.toCommand());
        return ResponseEntity.ok(ApiResponse.ok(ApprovePaymentResponseDto.from(payment)));
    }


}


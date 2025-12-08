package com.smore.payment.payment.presentation;

import com.smore.payment.payment.application.PaymentService;
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

    private final PaymentService paymentService;

    @PostMapping("/approve")
    public ResponseEntity<?> approvePayment(
            @Valid @RequestBody ApprovePaymentRequestDto request
    ) {
        Payment payment = paymentService.approve(request.toCommand());
        ApprovePaymentResponseDto response = new ApprovePaymentResponseDto(payment);
        return ResponseEntity.ok(response);
    }



}


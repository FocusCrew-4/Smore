package com.smore.payment.refundpolicy.presentation;

import com.smore.common.response.ApiResponse;
import com.smore.payment.global.config.UserContextHolder;
import com.smore.payment.refundpolicy.application.RefundPolicyService;
import com.smore.payment.refundpolicy.application.command.CreateRefundPolicyCommand;
import com.smore.payment.refundpolicy.application.query.GetRefundPolicyQuery;
import com.smore.payment.refundpolicy.domain.model.RefundPolicy;
import com.smore.payment.refundpolicy.presentation.dto.request.CreateRefundPolicyRequestDto;
import com.smore.payment.refundpolicy.presentation.dto.request.GetRefundPolicyRequestDto;
import com.smore.payment.refundpolicy.presentation.dto.response.GetRefundPolicyResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refund-policies")
public class RefundPolicyController {

    private final RefundPolicyService refundPolicyService;

    @PostMapping
    public ResponseEntity<?> createRefundPolicy(
            @Valid @RequestBody CreateRefundPolicyRequestDto createRefundPolicyRequestDto,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        UserContextHolder.set(userId);

        UUID id = refundPolicyService.createRefundPolicy(CreateRefundPolicyCommand.from(createRefundPolicyRequestDto));

        UserContextHolder.clear();

        return ResponseEntity.ok(ApiResponse.ok(id));
    }

    @GetMapping
    public ResponseEntity<?> getRefundPolicy(@Valid @RequestBody GetRefundPolicyRequestDto getRefundPolicyRequestDto) {

        RefundPolicy refundPolicy = refundPolicyService.getRefundPolicy(GetRefundPolicyQuery.from(getRefundPolicyRequestDto));

        return ResponseEntity.ok(ApiResponse.ok(GetRefundPolicyResponseDto.from(refundPolicy)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRefundPolicy(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        UserContextHolder.set(userId);
        refundPolicyService.deleteRefundPolicy(id, userId);
        UserContextHolder.clear();
        return ResponseEntity.ok().build();
    }
}

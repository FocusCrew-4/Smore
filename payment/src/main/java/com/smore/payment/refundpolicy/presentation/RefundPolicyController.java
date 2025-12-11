package com.smore.payment.refundpolicy.presentation;

import com.smore.common.response.ApiResponse;
import com.smore.payment.global.config.UserContextHolder;
import com.smore.payment.refundpolicy.application.RefundPolicyService;
import com.smore.payment.refundpolicy.application.query.GetRefundPolicyQuery;
import com.smore.payment.refundpolicy.domain.model.*;
import com.smore.payment.refundpolicy.presentation.dto.request.CreateRefundPolicyRequestDto;
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
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserContextHolder.set(userId);

        UUID id = refundPolicyService.createRefundPolicy(createRefundPolicyRequestDto.toCommand());

        UserContextHolder.clear();

        return ResponseEntity.ok(ApiResponse.ok(id));
    }

    @GetMapping
    public ResponseEntity<?> getRefundPolicy(
            @RequestParam String targetType,
            @RequestParam String targetKey
    ) {

        RefundTargetType refundTargetType = RefundTargetType.of(targetType);

        GetRefundPolicyQuery getCancelPolicyQuery = new GetRefundPolicyQuery(
                refundTargetType,
                switch (refundTargetType) {
                    case CATEGORY -> new TargetKeyUUID(UUID.fromString(targetKey));
                    case MERCHANT -> new TargetKeyLong(Long.parseLong(targetKey));
                    case AUCTION_TYPE -> new TargetKeyString(targetKey);
                    case USER_TYPE -> null;
                }
        );

        RefundPolicy refundPolicy = refundPolicyService.getRefundPolicy(getCancelPolicyQuery);

        return ResponseEntity.ok(ApiResponse.ok(GetRefundPolicyResponseDto.from(refundPolicy)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRefundPolicy(@PathVariable UUID id, @RequestHeader("X-User-Id") Long userId) {
        refundPolicyService.deleteRefundPolicy(id, userId);
        return ResponseEntity.ok().build();
    }
}

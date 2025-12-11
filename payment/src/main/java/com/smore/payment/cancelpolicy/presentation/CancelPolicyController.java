package com.smore.payment.cancelpolicy.presentation;

import com.smore.common.response.ApiResponse;
import com.smore.payment.cancelpolicy.application.CancelPolicyService;
import com.smore.payment.cancelpolicy.application.query.GetCancelPolicyQuery;
import com.smore.payment.cancelpolicy.domain.model.*;
import com.smore.payment.cancelpolicy.presentation.dto.request.CreateCancelPolicyRequestDto;
import com.smore.payment.cancelpolicy.presentation.dto.response.GetCancelPolicyResponseDto;
import com.smore.payment.global.config.UserContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cancel-policies")
public class CancelPolicyController {

    private final CancelPolicyService cancelPolicyService;

    @PostMapping
    public ResponseEntity<?> createCancelPolicy(
            @Valid @RequestBody CreateCancelPolicyRequestDto createCancelPolicyRequestDto,
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserContextHolder.set(userId);

        UUID id = cancelPolicyService.createCancelPolicy(createCancelPolicyRequestDto.toCommand());

        UserContextHolder.clear();

        return ResponseEntity.ok(ApiResponse.ok(id));
    }

    @GetMapping
    public ResponseEntity<?> getCancelPolicy(
            @RequestParam String targetType,
            @RequestParam String targetKey
    ) {

        CancelTargetType cancelTargetType = CancelTargetType.of(targetType);

        GetCancelPolicyQuery getCancelPolicyQuery = new GetCancelPolicyQuery(
                cancelTargetType,
                switch (cancelTargetType) {
                    case CATEGORY -> new TargetKeyUUID(UUID.fromString(targetKey));
                    case MERCHANT -> new TargetKeyLong(Long.parseLong(targetKey));
                    case AUCTION_TYPE -> new TargetKeyString(targetKey);
                    case USER_TYPE -> null;
                }
        );

        CancelPolicy cancelPolicy = cancelPolicyService.getCancelPolicy(getCancelPolicyQuery);

        return ResponseEntity.ok(ApiResponse.ok(GetCancelPolicyResponseDto.from(cancelPolicy)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCancelPolicy(@PathVariable UUID id, @RequestHeader("X-User-Id") Long userId) {
        UserContextHolder.set(userId);
        cancelPolicyService.deleteCancelPolicy(id, userId);
        UserContextHolder.clear();
        return ResponseEntity.ok().build();
    }
}

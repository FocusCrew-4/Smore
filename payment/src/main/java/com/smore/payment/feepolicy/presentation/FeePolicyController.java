package com.smore.payment.feepolicy.presentation;

import com.smore.common.response.ApiResponse;
import com.smore.payment.feepolicy.application.FeePolicyService;
import com.smore.payment.feepolicy.application.command.CreateFeePolicyCommand;
import com.smore.payment.feepolicy.application.query.GetFeePolicyQuery;
import com.smore.payment.feepolicy.domain.model.FeePolicy;
import com.smore.payment.feepolicy.domain.model.TargetKeyLong;
import com.smore.payment.feepolicy.domain.model.TargetKeyUUID;
import com.smore.payment.feepolicy.domain.model.TargetType;
import com.smore.payment.feepolicy.presentation.dto.request.CreateFeePolicyRequestDto;
import com.smore.payment.feepolicy.presentation.dto.request.GetFeePolicyRequestDto;
import com.smore.payment.feepolicy.presentation.dto.response.GetFeePolicyResponseDto;
import com.smore.payment.global.config.UserContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fee-policies")
public class FeePolicyController {

    private final FeePolicyService feePolicyService;

    @PostMapping
    public ResponseEntity<?> createFeePolicy(
            @Valid @RequestBody CreateFeePolicyRequestDto createFeePolicyDto,
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserContextHolder.set(userId);

        UUID id = feePolicyService.createFeePolicy(createFeePolicyDto.toCommand());

        UserContextHolder.clear();

        return ResponseEntity.ok(ApiResponse.ok(id));
    }

    @GetMapping
    public ResponseEntity<?> getFeePolicy(
            @RequestParam String targetType,
            @RequestParam String targetKey
    ) {

        TargetType type = TargetType.of(targetType);

        GetFeePolicyQuery getFeePolicyQuery = new GetFeePolicyQuery(
                type,
                switch (type) {
                    case CATEGORY -> new TargetKeyLong(Long.parseLong(targetKey));
                    case MERCHANT -> new TargetKeyUUID(UUID.fromString(targetKey));
                    case USER_TYPE -> null;
                }
        );
        FeePolicy feePolicy = feePolicyService.getFeePolicy(getFeePolicyQuery);

        return ResponseEntity.ok(ApiResponse.ok(GetFeePolicyResponseDto.from(feePolicy)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeePolicy(@PathVariable UUID id, @RequestHeader("X-User-Id") Long userId) {
        UserContextHolder.set(userId);
        feePolicyService.deleteFeePolicy(id, userId);
        UserContextHolder.clear();
        return ResponseEntity.ok().build();
    }
}

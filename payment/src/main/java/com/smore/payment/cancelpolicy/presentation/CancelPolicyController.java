package com.smore.payment.cancelpolicy.presentation;

import com.smore.common.response.ApiResponse;
import com.smore.payment.cancelpolicy.application.CancelPolicyService;
import com.smore.payment.cancelpolicy.application.command.CreateCancelPolicyCommand;
import com.smore.payment.cancelpolicy.application.query.GetCancelPolicyQuery;
import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.presentation.dto.request.CreateCancelPolicyRequestDto;
import com.smore.payment.cancelpolicy.presentation.dto.request.GetCancelPolicyRequestDto;
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
            @RequestHeader("X-User-Id") UUID userId
    ) {
        UserContextHolder.set(userId);

        UUID id = cancelPolicyService.createCancelPolicy(createCancelPolicyRequestDto.toCommand());

        UserContextHolder.clear();

        return ResponseEntity.ok(ApiResponse.ok(id));
    }

    @GetMapping
    public ResponseEntity<?> getCancelPolicy(@Valid @RequestBody GetCancelPolicyRequestDto getCancelPolicyRequestDto) {

        CancelPolicy cancelPolicy = cancelPolicyService.getCancelPolicy(getCancelPolicyRequestDto.toQuery());

        return ResponseEntity.ok(ApiResponse.ok(GetCancelPolicyResponseDto.from(cancelPolicy)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCancelPolicy(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        UserContextHolder.set(userId);
        cancelPolicyService.deleteCancelPolicy(id, userId);
        UserContextHolder.clear();
        return ResponseEntity.ok().build();
    }
}

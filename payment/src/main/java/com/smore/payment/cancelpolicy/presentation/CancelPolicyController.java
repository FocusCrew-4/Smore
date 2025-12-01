package com.smore.payment.cancelpolicy.presentation;

import com.smore.payment.cancelpolicy.domain.model.CancelPolicy;
import com.smore.payment.cancelpolicy.presentation.dto.CreateCancelPolicyRequestDto;
import com.smore.payment.cancelpolicy.presentation.dto.GetCancelPolicyRequest;
import com.smore.payment.feepolicy.application.FeePolicyService;
import com.smore.payment.feepolicy.application.command.CreateFeePolicyCommand;
import com.smore.payment.feepolicy.application.query.GetFeePolicyQuery;
import com.smore.payment.feepolicy.domain.model.*;
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
            @RequestHeader("X-USER-ID") UUID userId
    ) {
        UserContextHolder.set(userId);

        UUID id = cancelPolicyService.createCancelPolicy();

        UserContextHolder.clear();
        //Todo: 공통응답 사용해서 id 반환
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getCancelPolicy(@Valid @RequestBody GetCancelPolicyRequest getCancelPolicyRequest) {

        CancelPolicy cancelPolicy = cancelPolicyService.getCancelPolicy();

        //Todo: 공통응답 사용해서 응답 반환
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCancelPolicy(@PathVariable UUID id, @RequestHeader("X-USER-ID") UUID userId) {
        UserContextHolder.set(userId);
        cancelPolicyService.deleteCancelPolicy(id, userId);
        UserContextHolder.clear();
        return ResponseEntity.ok().build();
    }
}

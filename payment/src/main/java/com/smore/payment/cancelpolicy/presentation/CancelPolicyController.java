package com.smore.payment.cancelpolicy.presentation;

import com.smore.payment.cancelpolicy.application.CancelPolicyService;
import com.smore.payment.cancelpolicy.application.command.CreateCancelPolicyCommand;
import com.smore.payment.cancelpolicy.application.query.GetCancelPolicyQuery;
import com.smore.payment.cancelpolicy.domain.model.*;
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

        CreateCancelPolicyCommand command = new CreateCancelPolicyCommand(
                CancelTargetType.valueOf(createCancelPolicyRequestDto.getCancelTargetType()),
                createCancelPolicyRequestDto.getTargetKey(),
                createCancelPolicyRequestDto.getCancelLimitMinutes(),
                CancelFeeType.valueOf(createCancelPolicyRequestDto.getCancelFeeType()),
                new CancelFeeRate(createCancelPolicyRequestDto.getRate()),
                new CancelFixedAmount(createCancelPolicyRequestDto.getCancelFixedAmount()),
                createCancelPolicyRequestDto.isCancellable()
        );

        UUID id = cancelPolicyService.createCancelPolicy(command);

        UserContextHolder.clear();
        //Todo: 공통응답 사용해서 id 반환
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getCancelPolicy(@Valid @RequestBody GetCancelPolicyRequest getCancelPolicyRequest) {

        GetCancelPolicyQuery getCancelPolicyQuery = new GetCancelPolicyQuery(
                CancelTargetType.valueOf(getCancelPolicyRequest.getCancelTargetType()),
                getCancelPolicyRequest.getTargetKey()
        );

        CancelPolicy cancelPolicy = cancelPolicyService.getCancelPolicy(getCancelPolicyQuery);

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

package com.smore.payment.feepolicy.presentation;

import com.smore.payment.feepolicy.application.FeePolicyService;
import com.smore.payment.feepolicy.application.command.CreateFeePolicyCommand;
import com.smore.payment.feepolicy.application.query.GetFeePolicyQuery;
import com.smore.payment.feepolicy.domain.model.*;
import com.smore.payment.feepolicy.presentation.dto.CreateFeePolicyRequestDto;
import com.smore.payment.feepolicy.presentation.dto.GetFeePolicyRequest;
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
            @RequestHeader("X-USER-ID") UUID userId
    ) {
        UserContextHolder.set(userId);
        CreateFeePolicyCommand createFeePolicyCommand = new CreateFeePolicyCommand(
                TargetType.valueOf(createFeePolicyDto.getTargetType()),
                createFeePolicyDto.getTargetKey(),
                FeeType.valueOf(createFeePolicyDto.getFeeType()),
                new FeeRate(createFeePolicyDto.getRate()),
                new FixedAmount(createFeePolicyDto.getFixedAmount())
        );

        UUID id = feePolicyService.createFeePolicy(createFeePolicyCommand);

        UserContextHolder.clear();
        //Todo: 공통응답 사용해서 id 반환
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getFeePolicy(@Valid @RequestBody GetFeePolicyRequest getFeePolicyRequest) {

        GetFeePolicyQuery getFeePolicyQuery = new GetFeePolicyQuery(
                TargetType.valueOf(getFeePolicyRequest.getTargetType()),
                getFeePolicyRequest.getTargetKey()
        );

        FeePolicy feePolicy = feePolicyService.getFeePolicy(getFeePolicyQuery);

        //Todo: 공통응답 사용해서 응답 반환
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeePolicy(@PathVariable UUID id, @RequestHeader("X-USER-ID") UUID userId) {
        UserContextHolder.set(userId);
        feePolicyService.deleteFeePolicy(id, userId);
        UserContextHolder.clear();
        return ResponseEntity.ok().build();
    }
}

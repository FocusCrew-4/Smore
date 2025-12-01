package com.smore.payment.feepolicy.presentation;

import com.smore.payment.feepolicy.application.FeePolicyService;
import com.smore.payment.feepolicy.application.command.CreateFeePolicyCommand;
import com.smore.payment.feepolicy.domain.model.FeeRate;
import com.smore.payment.feepolicy.domain.model.FeeType;
import com.smore.payment.feepolicy.domain.model.FixedAmount;
import com.smore.payment.feepolicy.domain.model.TargetType;
import com.smore.payment.feepolicy.presentation.dto.CreateFeePolicyRequestDto;
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
    public ResponseEntity<?> createFeePolicy(@Valid @RequestBody CreateFeePolicyRequestDto createFeePolicyDto) {

        CreateFeePolicyCommand createFeePolicyCommand = new CreateFeePolicyCommand(
                TargetType.valueOf(createFeePolicyDto.getTargetType()),
                createFeePolicyDto.getTargetKey(),
                FeeType.valueOf(createFeePolicyDto.getFeeType()),
                new FeeRate(createFeePolicyDto.getRate()),
                new FixedAmount(createFeePolicyDto.getFixedAmount())
        );

        UUID id = feePolicyService.createFeePolicy(createFeePolicyCommand);

        //Todo: 공통응답 사용해서 id 반환
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeePolicy(@PathVariable UUID id) {

        feePolicyService.getFeePolicy(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeePolicy(@PathVariable UUID id) {

        feePolicyService.deleteFeePolicy(id);

        return ResponseEntity.ok().build();
    }
}

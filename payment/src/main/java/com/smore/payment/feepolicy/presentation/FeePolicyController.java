package com.smore.payment.feepolicy.presentation;

import com.smore.payment.feepolicy.presentation.dto.CreateFeePolicyRequestDto;
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
    public ResponseEntity<?> createFeePolicy(@RequestBody CreateFeePolicyRequestDto createFeePolicyDto) {

        feePolicyService.createFeePolicy(createFeePolicyDto);

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

package com.smore.payment.payment.domain.service;

import com.smore.payment.payment.application.facade.dto.FeePolicyResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SettlementAmountCalculator {

    public BigDecimal calculate(BigDecimal approvedAmount, FeePolicyResult policy) {
        BigDecimal fee;

        switch (policy.feeType()) {
            case "RATE" -> fee = approvedAmount.multiply(policy.rate());
            case "FIXED" -> fee = policy.fixedAmount();
            case "MIXED" -> fee = approvedAmount.multiply(policy.rate())
                    .add(policy.fixedAmount());
            default -> throw new IllegalArgumentException("Unknown feeType: " + policy.feeType());
        }

        return approvedAmount.subtract(fee);
    }
}
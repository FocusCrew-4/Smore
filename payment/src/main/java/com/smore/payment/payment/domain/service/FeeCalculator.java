package com.smore.payment.payment.domain.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FeeCalculator {

    public BigDecimal calculateSettlementAmount(BigDecimal approvedAmount, FeePolicyDecision decision) {
        BigDecimal fee;

        switch (decision.feeType()) {
            case "RATE" -> fee = approvedAmount.multiply(decision.rate());
            case "FIXED" -> fee = decision.fixedAmount();
            case "MIXED" -> fee = approvedAmount.multiply(decision.rate())
                    .add(decision.fixedAmount());
            default -> throw new IllegalArgumentException("Unknown feeType: " + decision.feeType());
        }

        return approvedAmount.subtract(fee);
    }
}
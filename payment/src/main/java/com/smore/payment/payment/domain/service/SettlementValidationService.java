package com.smore.payment.payment.domain.service;

import com.smore.payment.payment.application.event.inbound.PaymentSettlementRequestEvent;
import com.smore.payment.payment.domain.repository.SellerSettlementLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SettlementValidationService {

    private final SellerSettlementLedgerRepository sellerSettlementLedgerRepository;

    public SettlementValidationResult validate(PaymentSettlementRequestEvent event) {
        if (sellerSettlementLedgerRepository.existsByIdempotencyKey(event.idempotencyKey())) {
            return SettlementValidationResult.duplicated();
        }

        if (event.accountNumber() == null || event.accountNumber().isBlank()) {
            return SettlementValidationResult.invalid("계좌 번호가 잘못 입력되었습니다.");
        }

        BigDecimal balance = sellerSettlementLedgerRepository.calculateBalance(event.userId());
        if (balance.compareTo(event.amount()) < 0
                || balance.compareTo(BigDecimal.valueOf(1_000_000_000L)) > 0) {
            return SettlementValidationResult.invalid("출금 가능 잔액이 부족합니다.");
        }

        return SettlementValidationResult.valid();
    }
}
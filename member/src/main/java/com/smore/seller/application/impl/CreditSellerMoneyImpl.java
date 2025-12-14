package com.smore.seller.application.impl;

import com.smore.seller.application.usecase.CreditSellerMoney;
import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.repository.SellerRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NoContentException;
import java.math.BigDecimal;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionTimedOutException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CreditSellerMoneyImpl implements CreditSellerMoney {

    private final SellerRepository repository;
    private final Clock clock;

    // 트랜잭션을 너무 오래 잡고 있으면 안 됨
    // 재시도 시간이 너무 길어지면 Kafka message 진행이 안될 수 있음
    @Retryable(
        retryFor = {
            CannotAcquireLockException.class,
            TransactionTimedOutException.class
        },
        noRetryFor = {
            NoContentException.class,
            IllegalArgumentException.class
        },
        maxAttempts = 4,
        backoff = @Backoff(
            delay = 200,
            multiplier = 2,
            maxDelay = 1000
        )
    )
    @Override
    public void credit(Long targetId, BigDecimal amount) {
        Seller target =
            repository.findByMemberId(targetId);
        if (target == null) {
            throw new RuntimeException("찾는 회원이 존재하지 않습니다");
        }

        target.settle(amount, clock);
        log.info("고객의 금액 증가: {}", target.getMoney());

        repository.save(target);
    }
}

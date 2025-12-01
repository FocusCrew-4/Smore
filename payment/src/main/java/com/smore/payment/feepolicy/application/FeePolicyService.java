package com.smore.payment.feepolicy.application;

import com.smore.payment.feepolicy.application.command.CreateFeePolicyCommand;
import com.smore.payment.feepolicy.domain.model.FeePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FeePolicyService {

    private final FeePolicyRepository feePolicyRepository;

    public UUID createFeePolicy(CreateFeePolicyCommand createFeePolicyCommand) {

        FeePolicy feePolicy = FeePolicy.create(
                createFeePolicyCommand.targetType(),
                createFeePolicyCommand.targetKey(),
                createFeePolicyCommand.feeType(),
                createFeePolicyCommand.rate(),
                createFeePolicyCommand.fixedAmount()
        );

        feePolicyRepository.save(feePolicy);

        return feePolicy.getId();
    }

}

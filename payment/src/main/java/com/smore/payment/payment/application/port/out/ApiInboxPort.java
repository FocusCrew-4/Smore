package com.smore.payment.payment.application.port.out;

import com.smore.payment.payment.application.port.in.ApprovePaymentResult;

import java.util.Optional;

public interface ApiInboxPort {

    Optional<ApprovePaymentResult> find(String key);

    void save(String key, ApprovePaymentResult result);
}

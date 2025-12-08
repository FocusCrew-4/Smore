package com.smore.payment.payment.infrastructure.pg;

import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.domain.model.PgApproveResult;
import com.smore.payment.payment.infrastructure.pg.dto.TossApproveRequest;
import com.smore.payment.payment.infrastructure.pg.dto.TossApproveResponse;
import com.smore.payment.payment.infrastructure.pg.mapper.TossPgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TossPgClient implements PgClient {

    private final WebClient.Builder tossWebClientBuilder;

    @Override
    public PgApproveResult approve(String paymentKey, String pgOrderId, BigDecimal amount) {
        WebClient webClient = tossWebClientBuilder.build();
        try {
            TossApproveResponse response = webClient.post()
                    .uri("/v1/payments/confirm")
                    .bodyValue(new TossApproveRequest(paymentKey, pgOrderId, amount))
                    .retrieve()
                    .bodyToMono(TossApproveResponse.class)
                    .block();

            return TossPgMapper.toDomain(response);
        } catch (Exception e) {
            throw new RuntimeException("Toss PG 승인 실패", e);
        }
    }
}

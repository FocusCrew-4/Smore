package com.smore.payment.payment.infrastructure.pg;

import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.domain.model.PgApproveResult;
import com.smore.payment.payment.infrastructure.pg.dto.TossApproveRequest;
import com.smore.payment.payment.infrastructure.pg.dto.TossPaymentResponse;
import com.smore.payment.payment.infrastructure.pg.mapper.TossPgMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPgClient implements PgClient {

    private final WebClient tossWebClientBuilder;

    @Override
    public PgApproveResult approve(String paymentKey, String pgOrderId, BigDecimal amount) {
        try {
            TossPaymentResponse response = tossWebClientBuilder.post()
                    .uri("/v1/payments/confirm")
                    .bodyValue(new TossApproveRequest(paymentKey, pgOrderId, amount))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Toss PG 결제 승인 실패: {}", errorBody);
                                        return Mono.error(new RuntimeException("PG 승인 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(TossPaymentResponse.class)
                    .block();

            return TossPgMapper.toDomain(response);
        } catch (Exception e) {
            throw new RuntimeException("Toss PG 승인 실패", e);
        }
    }
}

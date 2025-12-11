package com.smore.bidcompetition.presentation.api;

import static com.smore.bidcompetition.presentation.auth.OrderRole.*;

import com.smore.bidcompetition.application.dto.CompetitionCommand;
import com.smore.bidcompetition.application.service.BidCompetitionService;
import com.smore.bidcompetition.presentation.auth.OrderRole;
import com.smore.bidcompetition.presentation.dto.BidRequest;
import com.smore.bidcompetition.presentation.dto.BidResponse;
import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BidControllerV1 implements BidController{

    private final BidCompetitionService service;

    @Override
    @PostMapping("/api/v1/external/bid-competitions")
    public ResponseEntity<CommonResponse<?>> requestOrder(
        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @Valid @RequestBody BidRequest request
    ) {

        OrderRole orderRole = from(role);

        if (orderRole.isNotAny(CONSUMER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("5403", "접근 권한이 없습니다."));
        }

        CompetitionCommand command = CompetitionCommand.of(
            requesterId,
            request.bidId(),
            request.quantity(),
            request.idempotencyKey(),
            request.street(),
            request.city(),
            request.zipcode()
        );

        BidResponse response = service.competition(command);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

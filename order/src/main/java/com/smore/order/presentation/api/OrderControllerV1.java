package com.smore.order.presentation.api;

import static com.smore.order.presentation.auth.OrderRole.*;

import com.smore.common.error.ErrorCode;
import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.member.domain.enums.Role;
import com.smore.order.application.service.OrderService;
import com.smore.order.presentation.auth.OrderRole;
import com.smore.order.presentation.dto.IsOrderCreatedResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV1 implements OrderController {

    private final OrderService orderService;

    @GetMapping("/api/v1/orders/by-token/{allocationToken}")
    public ResponseEntity<CommonResponse<?>> isOrderCreated(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        @PathVariable UUID allocationToken
    ) {
        OrderRole orderRole = from(role);

        if (orderRole.isNotAny(CONSUMER, SELLER, ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("5403", "접근 권한이 없습니다."));
        }

        IsOrderCreatedResponse response = orderService.isOrderCreated(allocationToken, requesterId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

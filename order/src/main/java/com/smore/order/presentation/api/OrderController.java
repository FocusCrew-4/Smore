package com.smore.order.presentation.api;

import com.smore.common.response.CommonResponse;
import com.smore.order.presentation.dto.RefundRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface OrderController {

    ResponseEntity<CommonResponse<?>> isOrderCreated(
        @NotNull(message = "userId는 필수값입니다.") @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @NotNull(message = "allocationToken은 필수값입니다.") @PathVariable UUID allocationToken
    );

    ResponseEntity<CommonResponse<?>> refundRequest(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        RefundRequest request);

}

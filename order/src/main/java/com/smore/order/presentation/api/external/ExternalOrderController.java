package com.smore.order.presentation.api.external;

import com.smore.common.response.CommonResponse;
import com.smore.order.presentation.dto.ModifyOrderRequest;
import com.smore.order.presentation.dto.RefundRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface ExternalOrderController {

    ResponseEntity<CommonResponse<?>> isOrderCreated(
        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @PathVariable UUID allocationToken
    );

    ResponseEntity<CommonResponse<?>> refundRequest(
        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @Valid @RequestBody RefundRequest request);

    ResponseEntity<CommonResponse<?>> modify(
        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @PathVariable UUID orderId,
        @Valid @RequestBody ModifyOrderRequest request
    );

    ResponseEntity<CommonResponse<?>> delete(
        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,

        @PathVariable UUID orderId
    );

    ResponseEntity<CommonResponse<?>> searchOrderOne(
        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @PathVariable UUID orderId
    );

    ResponseEntity<CommonResponse<?>> searchOrderList(

        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) UUID productId,
        @PageableDefault(size = 20)
        @SortDefault.SortDefaults({
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
            @SortDefault(sort = "orderedAt", direction = Sort.Direction.DESC),
            @SortDefault(sort = "totalAmount", direction = Sort.Direction.DESC)
        })
        Pageable pageable
    );
}

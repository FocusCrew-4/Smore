package com.smore.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smore.order.domain.vo.Address;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyOrderResponse {

    private final boolean isUpdate;

    private final UUID orderId;

    @JsonProperty("beforeShippingAddress")
    private final Address before;

    @JsonProperty("afterShippingAddress")
    private final Address after;

    private final String message;

    public static ModifyOrderResponse success(
        UUID orderId,
        String beforeStreet, String beforeCity, String beforeZipcode,
        String afterStreet, String afterCity, String afterZipcode
    ) {
        Address before = new Address(beforeStreet, beforeCity, beforeZipcode);
        Address after = new Address(afterStreet, afterCity, afterZipcode);

        return ModifyOrderResponse.builder()
            .isUpdate(true)
            .orderId(orderId)
            .before(before)
            .after(after)
            .message("주문 정보 수정에 성공했습니다")
            .build();
    }

    public static ModifyOrderResponse fail(UUID orderId, String message) {

        return ModifyOrderResponse.builder()
            .isUpdate(false)
            .orderId(orderId)
            .message(message)
            .build();
    }

}

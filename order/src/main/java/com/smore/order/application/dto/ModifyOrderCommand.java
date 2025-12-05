package com.smore.order.application.dto;

import com.smore.order.domain.vo.Address;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyOrderCommand {

    private UUID orderId;
    private Long userId;
    private Address address;

    public static ModifyOrderCommand of(
        UUID orderId,
        Long userId,
        String street, String city, String zipcode
    ) {

        Address address = new Address(street, city, zipcode);

        return ModifyOrderCommand.builder()
            .orderId(orderId)
            .userId(userId)
            .address(address)
            .build();
    }
}

package com.smore.bidcompetition.application.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetitionCommand {

    private Long userId;
    private UUID bidId;
    private Integer quantity;
    private UUID idempotencyKey;
    private String street;
    private String city;
    private String zipcode;

    public static CompetitionCommand of(
        Long userId,
        UUID bidId,
        Integer quantity,
        UUID idempotencyKey,
        String street,
        String city,
        String zipcode
    ) {
        if (userId == null) throw new IllegalArgumentException("userId는 필수값입니다");
        if (bidId == null) throw new IllegalArgumentException("bidId는 필수값입니다");
        if (quantity == null) throw new IllegalArgumentException("quantity는 필수값입니다");
        if (quantity < 1) throw new IllegalArgumentException("quantity는 1 이상이어야 합니다");
        if (idempotencyKey == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다");
        if (street == null) throw new IllegalArgumentException("street은 필수값입니다");
        if (city == null) throw new IllegalArgumentException("city는 필수값입니다");
        if (zipcode == null) throw new IllegalArgumentException("zipcode는 필수값입니다");

        return CompetitionCommand.builder()
            .userId(userId)
            .bidId(bidId)
            .quantity(quantity)
            .idempotencyKey(idempotencyKey)
            .street(street)
            .city(city)
            .zipcode(zipcode)
            .build();
    }
}

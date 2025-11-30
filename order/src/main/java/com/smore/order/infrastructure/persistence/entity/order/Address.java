package com.smore.order.infrastructure.persistence.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "zipcode", nullable = false)
    private String zipcode;

    protected Address(String street, String city, String zipcode) {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("도로 명 주소 필수 입력");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("지번 주소 필수 입력");
        if (zipcode == null || zipcode.isBlank()) throw new IllegalArgumentException("우편 번호 필수 입력");
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
    }

}

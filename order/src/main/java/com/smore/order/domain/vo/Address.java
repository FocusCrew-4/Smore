package com.smore.order.domain.vo;

public record Address(
    String street,
    String city,
    String zipcode
) {
    public Address {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("도로 명 주소 필수 입력");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("지번 주소 필수 입력");
        }
        if (zipcode == null || zipcode.isBlank()) {
            throw new IllegalArgumentException("우편 번호 필수 입력");
        }
    }
}

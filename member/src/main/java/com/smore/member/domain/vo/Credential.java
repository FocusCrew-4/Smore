package com.smore.member.domain.vo;

import java.util.regex.Pattern;

public record Credential(
    String email,
    String password
) {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Credential(
        String email,
        String password
    ) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
        this.password = password;
    }

}

package com.smore.member.infrastructure.persistance.jpa.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialEmbeddable {
    private String email;
    private String password;
}

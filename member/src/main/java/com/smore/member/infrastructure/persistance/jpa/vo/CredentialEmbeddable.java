package com.smore.member.infrastructure.persistance.jpa.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialEmbeddable {
    private String email;
    private String password;
}

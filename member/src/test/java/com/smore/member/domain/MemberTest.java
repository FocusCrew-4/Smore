package com.smore.member.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MemberTest {

    @Nested
        @DisplayName("멤버 create 테스트")
    class MemberCreate {
        @Test
        @DisplayName("id 는 null 이여야 한다")
        void idNull() {
            // given, when
            Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
            Member member = Member.create(
                Role.CONSUMER,
                "consumer@naver.com",
                "pw",
                "소비자",
                fixedClock
            );

            // then
            assertAll(
                () -> assertThat(member.getId()).isNull()
            );
        }

        @Test
        @DisplayName("createdAt 은 존재해야한다")
        void createdAt() {
            Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
            Member member = Member.create(
                Role.CONSUMER,
                "consumer@naver.com",
                "pw",
                "소비자",
                fixedClock
            );

            assertAll(
                () -> assertThat(member.getCreatedAt()).isNotNull()
            );
        }
    }

}

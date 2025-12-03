package com.smore.member.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.smore.member.domain.enums.Role;
import com.smore.member.domain.model.Member;
import com.smore.member.domain.repository.MemberRepository;
import com.smore.member.domain.vo.Credential;
import com.smore.member.infrastructure.config.TestContainerConfig;
import com.smore.member.infrastructure.persistence.config.QuerydslConfig;
import com.smore.member.infrastructure.persistence.jpa.mapper.MemberJpaMapperImpl;
import java.time.LocalDateTime;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    MemberJpaMapperImpl.class,
    MemberRepositoryImpl.class,
    QuerydslConfig.class,
    TestContainerConfig.class
})
class MemberRepositoryImplTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Settings nonNullSettings = Settings.create()
            .set(Keys.STRING_NULLABLE, false);

        Member member = Instancio.of(Member.class)
            .withSettings(nonNullSettings)
            .set(Select.field(Member.class, "id"), null)
            .set(Select.field(Member.class, "role"), Role.CONSUMER)
            .set(Select.field(Member.class, "createdAt"), LocalDateTime.now())
            .set(Select.field(Member.class, "updatedAt"), LocalDateTime.now())
            .supply(Select.all(Credential.class),
                () -> new Credential("instancio@example.com", "password123!"))
            .create();

        memberRepository.save(member);
    }

    @Test
    void findById() {
        // when
        Member findMember = memberRepository.findByEmail("instancio@example.com");

        // then
        assertAll(
            () -> assertNotNull(findMember),
            () -> assertNotNull(findMember.getId())
        );
    }
}

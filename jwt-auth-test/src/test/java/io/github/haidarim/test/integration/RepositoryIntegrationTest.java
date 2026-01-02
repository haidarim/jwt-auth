package io.github.haidarim.test.integration;
import io.github.haidarim.common.AbstractJwtTest;
import io.github.haidarim.entity.TestUser;
import io.github.haidarim.repository.TestUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RepositoryIntegrationTest extends AbstractJwtTest {

    @Autowired
    private TestUserRepository userRepository;


    @Test
    void testSaveAndFindUser() {
        TestUser user = TestUser.builder()
                .email("user@example.com")
                .username("user1")
                .passwordHash("pass")
                .uniqueNumber("1234")
                .salt("salt")
                .build();

        userRepository.save(user);

        TestUser fetched = userRepository.findByEmail("user@example.com");
        assertThat(fetched).isNotNull();
        assertThat(fetched.getUsername()).isEqualTo("user1");
    }
}

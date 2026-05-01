/*
 * Copyright (c) 2026 haidarim
 * All rights reserved.
 *
 * This software is provided for personal, non-commercial use only.
 *
 * Unauthorized copying, modification, redistribution, or use in
 * commercial products or services is strictly prohibited.
 *
 * You may fork and modify this code solely for the purpose of
 * contributing bug fixes or improvements back to the original
 * repository via pull requests.
 *
 * All other uses require explicit written permission from the author.
 */

package io.github.haidarim.test.integration;
import io.github.haidarim.common.AbstractJwtTest;
import io.github.haidarim.entity.TestUser;
import io.github.haidarim.repository.TestUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        Optional<TestUser> fetched = userRepository.findByEmail("user@example.com");
        assertTrue(fetched.isPresent());
        assertThat(fetched.get().getUsername()).isEqualTo("user1");
    }
}

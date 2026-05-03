/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
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

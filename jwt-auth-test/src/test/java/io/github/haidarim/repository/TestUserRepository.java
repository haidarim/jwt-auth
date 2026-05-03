/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.repository;

import io.github.haidarim.entity.TestUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface TestUserRepository extends JpaRepository<@NotNull TestUser, @NotNull Long> {

    @Query("SELECT u FROM TestUser u WHERE u.email = :email")
    Optional<TestUser> findByEmail(@Param("email") String email);

    @Query("SELECT u.email FROM TestUser u WHERE u.username = :username")
    Optional<String> findEmailByUsername(@Param("username") String username);

    @Query("SELECT u.email FROM TestUser u WHERE u.uniqueNumber = :uniqueNumber")
    Optional<String> findEmailByUniqueNumber(@Param("uniqueNumber") String uniqueNumber);
}

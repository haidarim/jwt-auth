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

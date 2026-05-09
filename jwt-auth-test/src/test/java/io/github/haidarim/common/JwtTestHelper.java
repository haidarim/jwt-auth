/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.common;

import io.github.haidarim.api.Role;
import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.api.service.TokenRevocationService;
import io.github.haidarim.entity.TestUser;
import io.github.haidarim.impl.config.JwtConfig;
import io.github.haidarim.repository.TestUserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

@TestComponent
public class JwtTestHelper {

    @Autowired
    TestUserRepository userRepository;
    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    JwtService jwtService;
    @Autowired
    TokenRevocationService tokenRevocationService;
    @Autowired
    PasswordEncoder passwordEncoder;


    public void assertUserExistence(boolean shouldExist, String email){
        assertEquals(shouldExist, userRepository.findByEmail(email).isPresent());
    }

    public void removeTestUser(String email) {
        Optional<TestUser> user = userRepository.findByEmail(email);
        assertTrue(user.isPresent());
        userRepository.delete(user.get());
        assertUserExistence(false, user.get().getEmail());
    }

    public void deleteAllTestUsers(){
        userRepository.deleteAll();
    }

    public void createTestUser(String username, String email, String password, String uniqueNumber){
        TestUser user = TestUser
                .builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .uniqueNumber(uniqueNumber)
                .salt("DUMMY_VALUE")
                .build();
        user.setRole(Role.USER);
        userRepository.save(user);
        assertUserExistence(true, email);
    }

    public long getJwtTimeoutMillis(){
        return jwtConfig.getExpirationMillis();
    }

    public void setJwtTimeoutMillis(long timeoutMillis){
        jwtConfig.setExpirationMillis(timeoutMillis);
    }

    public void setCheckExpiration(boolean checkExpiration){
        jwtConfig.setCheckExpiration(checkExpiration);
    }

    public void sleepUntil(BooleanSupplier condition, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();

        while (!condition.getAsBoolean()) {
            long remainingNanos = deadline - System.nanoTime();
            if (remainingNanos <= 0) {
                fail("Condition was not met within " + timeout);
            }

            try {
                // sleep min(25ms, remaining time)
                Thread.sleep(Math.min(25, remainingNanos / 1_000_000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Test interrupted");
            }
        }
    }

    public long getJwtExpirationTime(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return jwtService.getClaim(token, Claims::getExpiration).getTime();
    }

    public String getJti(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return jwtService.getClaim(token, Claims::getId);
    }

    public boolean isTokenRevoked(String jti){
        return tokenRevocationService.isTokenRevoked(jti);
    }
}

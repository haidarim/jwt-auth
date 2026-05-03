/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.service;

import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.impl.config.JwtConfig;
import io.github.haidarim.impl.service.DefaultJwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import static io.github.haidarim.api.JwtAlgorithm.HS256;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultJwtServiceTest {

    private JwtConfig jwtConfig;

    private JwtService jwtService;

    @BeforeEach
    void setUp(){
        // 32-byte secret for HS256
        byte[] secretBytes = new byte[32];
        for (int i = 0; i < secretBytes.length; i++) secretBytes[i] = (byte) i;

        String encodedSecret = java.util.Base64.getEncoder().encodeToString(secretBytes);
        jwtConfig = new JwtConfig(
                HS256,
                encodedSecret,
                null,
                null,
                true,
                3600000L,
                0L
        );

        jwtConfig.addIssuer("orelease.com");
        jwtConfig.addAudience("test");
        jwtService = new DefaultJwtServiceImpl(jwtConfig);
    }


    @Test
    void testGenerateTokenAndVerify() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "test-user";

        // Create token
        String token = jwtService.createToken(username, new HashMap<>(), "orelease.com", "test");
        assertNotNull(token, "Token should not be null");

        // Verify token validity
        boolean valid = jwtService.isTokenValid(token, username);
        assertTrue(valid, "Token should be valid");

        // Verify subject extraction
        String subject = jwtService.getSubject(token);
        assertEquals(username, subject, "Subject should match username");
    }

    @Test
    void testTokenInvalidForDifferentUser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = "test-user";
        String token = jwtService.createToken(username, new HashMap<>(), "orelease.com", "test");

        // Token should be invalid for another username
        boolean valid = jwtService.isTokenValid(token, "other-user");
        assertFalse(valid, "Token should be invalid for a different username");
    }

    @Test
    void testExpiredToken() throws InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        jwtConfig.setExpirationMillis(1L);
        jwtService = new DefaultJwtServiceImpl(jwtConfig);

        String token = jwtService.createToken("test-user", new HashMap<>(), "orelease.com", "test");

        Thread.sleep(10);

        boolean valid = jwtService.isTokenValid(token, "test-user");
        assertFalse(valid, "Token should be expired");
    }

    @Test
    void validationFailsForInvalidIssuer() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create token
        String token = jwtService.createToken("test-user", new HashMap<>(), "whatever.com", "test");
        assertNotNull(token, "Token should not be null");

        // Verify token validity
        boolean valid = jwtService.isTokenValid(token, "test-user");
        assertFalse(valid, "Token should be invalid");
    }

    @Test
    void validationFailsForInvalidAudience() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create token
        String token = jwtService.createToken("test-user", new HashMap<>(), "orelease.com", "whatever");
        assertNotNull(token, "Token should not be null");

        // Verify token validity
        boolean valid = jwtService.isTokenValid(token, "test-user");
        assertFalse(valid, "Token should be invalid");
    }

}

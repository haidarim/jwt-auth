package io.github.haidarim.service;

import io.github.haidarim.impl.config.JwtConfig;
import io.github.haidarim.impl.service.DefaultJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultJwtServiceTest {

    @Mock
    private JwtConfig jwtConfig;

    private DefaultJwtService jwtService;

    @BeforeEach
    void setUp(){
        // 32-byte secret for HS256
        byte[] secretBytes = new byte[32];
        for (int i = 0; i < secretBytes.length; i++) secretBytes[i] = (byte) i;

        String encodedSecret = java.util.Base64.getEncoder().encodeToString(secretBytes);
        when(jwtConfig.getHsSecret()).thenReturn(encodedSecret);

        when(jwtConfig.getAlgorithm()).thenReturn("HS256");
        when(jwtConfig.isCheckExpiration()).thenReturn(true);
        when(jwtConfig.getExpirationMillis()).thenReturn(3600000L);

        jwtService = new DefaultJwtService(jwtConfig);
    }


    @Test
    void testGenerateTokenAndVerify() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "test-user";

        // Create token
        String token = jwtService.createToken(username);
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
        String token = jwtService.createToken(username);

        // Token should be invalid for another username
        boolean valid = jwtService.isTokenValid(token, "other-user");
        assertFalse(valid, "Token should be invalid for a different username");
    }

    @Test
    void testExpiredToken() throws InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        when(jwtConfig.getExpirationMillis()).thenReturn(1L);
        jwtService = new DefaultJwtService(jwtConfig);

        String token = jwtService.createToken("test-user");

        Thread.sleep(10);

        boolean valid = jwtService.isTokenValid(token, "test-user");
        assertFalse(valid, "Token should be expired");
    }


}

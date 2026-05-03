/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.test.system;

import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.common.AbstractJwtTest;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;

import static io.github.haidarim.common.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class JwtAuthenticationSystemTest extends AbstractJwtTest {

    @Test
    public void registerUserSuccessfullyTest(){
        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);
        assertTrue(token != null && !token.trim().isEmpty());

        testHelper.assertUserExistence(true, EMAIL_1);
    }

    @Test
    public void unauthenticatedUserGetsUnauthorizedForProtectedResourceTest(){
        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    public void unregisteredUserAuthenticationFailsTest(){
        webTestClient.post()
                .uri("/api/v0/auth/authenticate")
                .bodyValue(new RegisterRequest(
                        USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE
                ))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void registeredUserWithValidJwtShouldAccessProtectedResourceTest(){
        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);
        String responseMessage = webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Hello!", responseMessage);
    }

    @Test
    public void deletedUserWithValidJwtAccessProtectedResourceFailsTest(){
        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);

        testHelper.removeTestUser(EMAIL_1);

        webTestClient.post()
                .uri("/api/v0/auth/authenticate")
                .bodyValue(new RegisterRequest(
                        USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE
                ))
                .exchange()
                .expectStatus()
                .isBadRequest();

        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    public void registeredUserWithExpiredJwtAccessProtectedResourceFailsTest(){
        testHelper.setJwtTimeoutMillis(3_000L);

        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);

        // Assert: token works initially
        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        // Act: wait until token is expired
        testHelper.sleepUntil(
                () -> {
                    try {
                        return System.currentTimeMillis() >= testHelper.getJwtExpirationTime(token);
                    } catch (Exception e) {
                        return true;
                    }
                },
                Duration.ofSeconds(5)
        );

        // Assert: token no longer works
        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void registeredUserRequestsNewJwtSuccessfullyTest() {
        testHelper.createTestUser(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);

        String token = assertAuthenticationRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);

        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .equals("Hello!");
    }

    @Test
    public void userJwtShouldBeUniqueTest(){
        String tokenUser1 = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);

        String tokenUser2 = assertRegisterRequestAndGetToken(USERNAME_2, EMAIL_2, PASSWORD_2, UNIQUE_NUM_2, VALID_ISSUER, VALID_AUDIENCE);

        assertNotEquals(tokenUser1, tokenUser2);
    }

    @Test
    public void userShouldBeAbleToGetJwtUsingAllowedCredentialFormsTest(){
        testHelper.createTestUser(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);
        assertAuthenticationRequestAndGetToken(null, null, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);
        assertAuthenticationRequestAndGetToken(null, EMAIL_1, PASSWORD_1, null, VALID_ISSUER, VALID_AUDIENCE);
        assertAuthenticationRequestAndGetToken(USERNAME_1, null, PASSWORD_1, null, VALID_ISSUER, VALID_AUDIENCE);
    }

    @Test
    public void tokenShouldBeRevokedAfterLogoutTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String token= assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1, VALID_ISSUER, VALID_AUDIENCE);
        webTestClient
                .post()
                .uri("/api/v0/auth/logout")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isOk();
        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isUnauthorized();
        String jti = testHelper.getJti(token);
        assertTrue(testHelper.isTokenRevoked(jti));
    }

    private String assertRegisterRequestAndGetToken(String username, String email, String password, String uniqueNumber, String issuer, String audience){
        AuthenticationResponse response = webTestClient.post()
                .uri("/api/v0/auth/register")
                .bodyValue(new RegisterRequest(
                        username, email, password, uniqueNumber, issuer, audience
                ))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AuthenticationResponse.class)
                .returnResult()
                .getResponseBody();
        return response.getToken();
    }

    private String assertAuthenticationRequestAndGetToken(String username, String email, String password, String uniqueNumber, String issuer, String audience){
        AuthenticationResponse response = webTestClient.post()
                .uri("/api/v0/auth/authenticate")
                .bodyValue(new RegisterRequest(
                        username, email, password, uniqueNumber, issuer, audience
                ))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AuthenticationResponse.class)
                .returnResult()
                .getResponseBody();

        return response.getToken();
    }
}

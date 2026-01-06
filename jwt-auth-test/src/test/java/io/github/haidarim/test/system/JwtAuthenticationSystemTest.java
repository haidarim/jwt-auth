package io.github.haidarim.test.system;

import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.common.AbstractJwtTest;
import io.github.haidarim.common.JwtTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class JwtAuthenticationSystemTest extends AbstractJwtTest {
    private final String USERNAME_1 = "u1";
    private final String USERNAME_2 = "u2";
    private final String EMAIL_1 = "user1@example.com";
    private final String EMAIL_2 = "user2@example.com";
    private final String PASSWORD_1 = "pass1";
    private final String PASSWORD_2 = "pass2";
    private final String UNIQUE_NUM_1 = "1001";
    private final String UNIQUE_NUM_2 = "1002";

    @Test
    public void registerUserSuccessfullyTest(){
        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);
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
                        USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1
                ))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void registeredUserWithValidJwtShouldAccessProtectedResourceTest(){
        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);
        String responseMessage = webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authentication", "Bearer " + token)
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
        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);

        testHelper.removeTestUser(EMAIL_1);

        webTestClient.post()
                .uri("/api/v0/auth/authenticate")
                .bodyValue(new RegisterRequest(
                        USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1
                ))
                .exchange()
                .expectStatus()
                .isBadRequest();

        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authentication", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    public void registeredUserWithExpiredJwtAccessProtectedResourceFailsTest(){
        testHelper.setJwtTimeoutMillis(1_000L);

        String token = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);

        // Assert: token works initially
        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authentication", "Bearer " + token)
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
                Duration.ofSeconds(3)
        );

        // Assert: token no longer works
        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authentication", "Bearer " + token)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void registeredUserRequestsNewJwtSuccessfullyTest() {
        testHelper.createTestUser(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);
        AuthenticationResponse response = webTestClient.post()
                .uri("/api/v0/auth/authenticate")
                .bodyValue(new RegisterRequest(
                        USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthenticationResponse.class)
                .returnResult()
                .getResponseBody();

        String token = response.getToken();

        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .header("Authentication", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .equals("Hello!");
    }

    @Test
    public void userJwtShouldBeUniqueTest(){
        String tokenUser1 = assertRegisterRequestAndGetToken(USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1);

        String tokenUser2 = assertRegisterRequestAndGetToken(USERNAME_2, EMAIL_2, PASSWORD_2, UNIQUE_NUM_2);

        assertNotEquals(tokenUser1, tokenUser2);
    }

    @Test
    public void userShouldBeAbleToGetJwtUsingAllowedCredentialFormsTest(){

    }

    @Test
    public void userAuthenticationWithInvalidPasswordFailsTest(){

    }

    private String assertRegisterRequestAndGetToken(String username, String email, String password, String uniqueNumber){
        AuthenticationResponse response = webTestClient.post()
                .uri("/api/v0/auth/register")
                .bodyValue(new RegisterRequest(
                        username, email, password, uniqueNumber
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

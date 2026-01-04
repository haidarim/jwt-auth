package io.github.haidarim.test.system;

import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.common.AbstractJwtTest;
import io.github.haidarim.common.JwtTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        AuthenticationResponse response =
                webTestClient.post()
                        .uri("/api/v0/auth/register")
                        .bodyValue(new RegisterRequest(
                                USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1
                        ))
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody();

        String token = response.getToken();
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
        AuthenticationResponse response =
                webTestClient.post()
                        .uri("/api/v0/auth/register")
                        .bodyValue(new RegisterRequest(
                                USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1
                        ))
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody();

        String token = response.getToken();
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
        AuthenticationResponse response =
                webTestClient.post()
                        .uri("/api/v0/auth/register")
                        .bodyValue(new RegisterRequest(
                                USERNAME_1, EMAIL_1, PASSWORD_1, UNIQUE_NUM_1
                        ))
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody();

        String token = response.getToken();

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

    }

    @Test
    public void registeredUserRequestsNewJwtSuccessfullyTest(){

    }

    @Test
    public void userJwtShouldBeUniqueTest(){

    }

    @Test
    public void userShouldBeAbleToGetJwtUsingAllowedCredentialFormsTest(){

    }
}

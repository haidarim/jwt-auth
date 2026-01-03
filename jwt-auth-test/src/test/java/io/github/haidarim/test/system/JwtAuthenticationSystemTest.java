package io.github.haidarim.test.system;

import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.common.AbstractJwtTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtAuthenticationSystemTest extends AbstractJwtTest {


    @Test
    public void registerUserTest(){
        AuthenticationResponse auth =
                webTestClient.post()
                        .uri("/api/v0/auth/register")
                        .bodyValue(new RegisterRequest(
                                "u1", "user1@example.com", "pass", "1001"
                        ))
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthenticationResponse.class)
                        .returnResult()
                        .getResponseBody();

        String token = auth.getToken();
        assertTrue(token != null && !token.trim().isEmpty());
    }

    @Test
    public void unauthenticatedUserTest(){
        webTestClient.get()
                .uri("/api/v0/auth/get/hello-message")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}

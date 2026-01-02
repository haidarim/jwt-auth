package io.github.haidarim.test.system;

import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.common.AbstractJwtTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtAuthenticationSystemTest extends AbstractJwtTest {

    @Test
    public void unauthenticatedUserTest(){

    }

    @Test
    public void registerUserTest(){
        RegisterRequest request = new RegisterRequest(
                "u1",
                "user1@exemple.com",
                "u1pass",
                "1001"
        );

        EntityExchangeResult<@NotNull AuthenticationResponse> response = restClient
                .post()
                .uri("/api/v0/auth/register")
                .body(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(AuthenticationResponse.class)
                .returnResult();
        assertNotNull(response);

        assertEquals(HttpStatus.OK, response.getStatus());

        String token = response.getResponseBody().getToken();

        // add other path to be authenticated for access
    }

    @Test
    public void nonExistingUserTest(){

    }

}

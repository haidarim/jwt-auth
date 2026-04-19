package io.github.haidarim.api.service;

import io.github.haidarim.api.dto.request.AuthenticationRequest;
import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;

/**
 * JwtAuthenticationService
 */
public interface JwtAuthenticationService {
    /**
     * Send authentication request
     * @param request AuthenticationRequest
     * @return response AuthenticationResponse
     * @throws Exception if authentication request fails
     */
    AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception;

    /**
     * Send register request
     * @param request RegisterRequest
     * @return response AuthenticationResponse
     * @throws Exception if register request fails
     */
    AuthenticationResponse register(RegisterRequest request) throws Exception;
}

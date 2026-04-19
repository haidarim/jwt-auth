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

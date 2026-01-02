package io.github.haidarim.api.service;

import io.github.haidarim.api.dto.request.AuthenticationRequest;
import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;

/**
 *
 */
public interface JwtAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception;
    AuthenticationResponse register(RegisterRequest request) throws Exception;
}

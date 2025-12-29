package io.github.haidarim.api.service;

import io.github.haidarim.api.dto.request.AuthenticationRequest;
import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;

/**
 *
 */
public interface JwtAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse register(RegisterRequest request);
}

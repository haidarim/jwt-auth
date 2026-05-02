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

package io.github.haidarim.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AuthenticationRequest Dto
 *
 * <p>Represents authentication request data including username,
 *  email, unique identifier, password, and token.</p>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthenticationRequest {
    private String username;
    private String email;
    private String uniqueNumber;
    private String password;
    private String token;
    private String issuer;
    private String audience;
}